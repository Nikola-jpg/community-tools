package com.community.tools.service.slack;

import static com.community.tools.util.statemachie.Event.SEND_ESTIMATE_TASK;

import com.community.tools.model.User;
import com.community.tools.service.EstimateTaskService;
import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.payload.EstimatePayload;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.service.payload.SinglePayload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;

import com.github.seratch.jslack.api.model.event.MessageEvent;
import com.github.seratch.jslack.app_backend.events.EventsDispatcher;
import com.github.seratch.jslack.app_backend.events.handler.MessageHandler;
import com.github.seratch.jslack.app_backend.events.handler.TeamJoinHandler;
import com.github.seratch.jslack.app_backend.events.payload.MessagePayload;
import com.github.seratch.jslack.app_backend.events.payload.TeamJoinPayload;
import com.github.seratch.jslack.app_backend.events.servlet.SlackEventsApiServlet;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SlackHandlerService {

  @Value("${notThatMessage}")
  private String notThatMessage;
  @Value("${welcome}")
  private String welcome;
  @Value("${messageAboutRules}")
  private String messageAboutRules;
  @Value("${idOfSlackBot}")
  private String idOfSlackBot;
  @Value("${defaultMessage}")
  private String defaultMessage;
  @Value("${testModeSwitcher}")
  private Boolean testModeSwitcher;
  @Value("${estimateTheTask}")
  private String estimateTheTask;
  @Value("${chooseAnAnswer}")
  private String chooseAnAnswer;

  private final MessageService messageService;
  private final StateMachineService stateMachineService;
  @Autowired
  private StateMachineRepository stateMachineRepository;
  @Autowired
  private EstimateTaskService estimateTaskService;

  private TeamJoinHandler teamJoinHandler = new TeamJoinHandler() {
    @Override
    public void handle(TeamJoinPayload teamJoinPayload) {

      try {
        String user = teamJoinPayload.getEvent().getUser().getId();
        User stateEntity = new User();
        stateEntity.setUserID(user);
        stateMachineRepository.save(stateEntity);

        stateMachineService.persistMachineForNewUser(user);
        messageService.sendPrivateMessage(teamJoinPayload.getEvent().getUser().getRealName(),
            welcome);
        messageService
            .sendBlocksMessage(teamJoinPayload.getEvent().getUser().getRealName(),
                messageAboutRules);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  };

  /**
   * Reset User with Slack id.
   *
   * @param id Slack id
   * @throws Exception Exception
   */
  public void resetUser(String id) throws Exception {

    User stateEntity = new User();
    stateEntity.setUserID(id);
    stateMachineRepository.save(stateEntity);
    stateMachineService.persistMachineForNewUser(id);

    String user = messageService.getUserById(id);
    messageService.sendPrivateMessage(user,
        welcome);
    messageService
        .sendBlocksMessage(user,
            messageAboutRules);
  }

  private MessageHandler messageHandler = new MessageHandler() {
    @Override
    public void handle(MessagePayload teamJoinPayload) {
      MessageEvent messageEvent = teamJoinPayload.getEvent();
      if (!messageEvent.getUser().equals(idOfSlackBot)) {
        try {
          if (messageEvent.getText().equals("reset") && testModeSwitcher) {
            resetUser(messageEvent.getUser());
          } else {

            String id = messageEvent.getUser();
            StateMachine<State, Event> machine = stateMachineService
                .restoreMachine(id);

            String userForQuestion = machine.getExtendedState().getVariables().get("id").toString();

            String message = defaultMessage;
            Event event = null;
            Payload payload = null;
            switch (machine.getState().getId()) {
              case NEW_USER:
                if (messageEvent.getText().equals("ready")) {
                  payload = new SinglePayload(id);
                  event = Event.QUESTION_FIRST;
                } else {
                  message = notThatMessage;
                }
                break;
              case FIRST_QUESTION:
                payload = new QuestionPayload(id, messageEvent.getText(), userForQuestion);
                event = Event.QUESTION_SECOND;
                break;
              case SECOND_QUESTION:
                payload = new QuestionPayload(id, messageEvent.getText(), userForQuestion);
                event = Event.QUESTION_THIRD;
                break;
              case THIRD_QUESTION:
                payload = new QuestionPayload(id, messageEvent.getText(), userForQuestion);
                event = Event.CONSENT_TO_INFORMATION;
                break;
              case AGREED_LICENSE:
                String gitNick = messageEvent.getText();
                payload = new VerificationPayload(id, gitNick);
                event = Event.LOGIN_CONFIRMATION;
                break;
              case CHECK_LOGIN:
                if (messageEvent.getText().equals("yes")) {
                  event = Event.ADD_GIT_NAME_AND_FIRST_TASK;
                  payload = (VerificationPayload) machine.getExtendedState().getVariables()
                      .get("dataPayload");
                } else if (messageEvent.getText().equals("no")) {
                  event = Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
                  payload = new SinglePayload(id);
                } else {
                  message = notThatMessage;
                }
                break;
              case ESTIMATE_THE_TASK:
                int value = Integer.parseInt(messageEvent.getText());
                if (value > 0 && value < 6) {
                  event = Event.CONFIRM_ESTIMATE;
                  payload = new EstimatePayload(id, value);
                } else {
                  message = chooseAnAnswer;
                }
                break;
              case GOT_THE_TASK:
                if (messageEvent.getText().equals("yes")) {
                  estimateTaskService.estimate(id);
                  return;
                } else if (messageEvent.getText().equals("no")) {
                  event = SEND_ESTIMATE_TASK;
                  payload = new SinglePayload(id);
                }
                break;
              default:
                event = null;
                payload = null;
            }

            if (event == null) {
              messageService.sendBlocksMessage(
                  messageService.getUserById(messageEvent.getUser()), message);
            } else {
              stateMachineService
                  .doAction(machine, payload, event);
            }
          }
        } catch (Exception e) {
          throw new RuntimeException("Impossible to answer request with id="
              + teamJoinPayload.getEvent().getUser(), e);
        }
      }
    }
  };

  public class GreatNewMemberServlet extends SlackEventsApiServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      super.doPost(req, resp);
    }

    @Override
    protected void setupDispatcher(EventsDispatcher dispatcher) {
      if (!testModeSwitcher) {
        dispatcher.register(teamJoinHandler);
      }
      dispatcher.register(messageHandler);
    }
  }

  @Bean
  public ServletRegistrationBean<GreatNewMemberServlet> servletRegistrationBean() {
    return new ServletRegistrationBean<>(new GreatNewMemberServlet(), "/greatNewMember/*");
  }
}