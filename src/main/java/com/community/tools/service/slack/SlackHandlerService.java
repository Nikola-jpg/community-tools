package com.community.tools.service.slack;

import static com.community.tools.util.statemachie.Event.CHANNELS_INFORMATION;
import static com.community.tools.util.statemachie.Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
import static com.community.tools.util.statemachie.Event.QUESTION_SECOND;
import static com.community.tools.util.statemachie.Event.QUESTION_THIRD;

import com.community.tools.model.User;
import com.community.tools.service.StateMachineService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;

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
  @Value("${usersAgreeMessage}")
  private String usersAgreeMessage;
  @Value("${defaultMessage}")
  private String defaultMessage;
  @Value("${testModeSwitcher}")
  private Boolean testModeSwitcher;

  private final SlackService slackService;
  private final StateMachineService stateMachineService;
  @Autowired
  private StateMachineRepository stateMachineRepository;

  private TeamJoinHandler teamJoinHandler = new TeamJoinHandler() {
    @Override
    public void handle(TeamJoinPayload teamJoinPayload) {

      try {
        String user = teamJoinPayload.getEvent().getUser().getId();
        User stateEntity = new User();
        stateEntity.setUserID(user);
        stateMachineRepository.save(stateEntity);

        stateMachineService.persistMachineForNewUser(user);
        slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser().getRealName(),
                welcome);
        slackService
                .sendBlocksMessage(teamJoinPayload.getEvent().getUser().getRealName(),
                        messageAboutRules);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  };

  /**
   * Reset User with Slack id.
   * @param id Slack id
   * @throws Exception Exception
   */
  public void resetUser(String id) throws Exception {

    User stateEntity = new User();
    stateEntity.setUserID(id);
    stateMachineRepository.save(stateEntity);
    stateMachineService.persistMachineForNewUser(id);

    String user = slackService.getUserById(id);
    slackService.sendPrivateMessage(user,
            welcome);
    slackService
            .sendBlocksMessage(user,
                    messageAboutRules);
  }

  private MessageHandler messageHandler = new MessageHandler() {
    @Override
    public void handle(MessagePayload teamJoinPayload) {
      User stateEntity;
      if (!teamJoinPayload.getEvent().getUser().equals(idOfSlackBot)) {
        try {
          if (teamJoinPayload.getEvent().getText().equals("reset") && testModeSwitcher) {
            resetUser(teamJoinPayload.getEvent().getUser());
          }
          StateMachine<State, Event> machine = stateMachineService
                  .restoreMachine(teamJoinPayload.getEvent().getUser());

          String user = machine.getExtendedState().getVariables().get("id").toString();

          switch (machine.getState().getId()) {
            case NEW_USER:
              if (teamJoinPayload.getEvent().getText().equals("ready")) {
                machine.sendEvent(Event.QUESTION_FIRST);
                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              } else {
                slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser(),
                        notThatMessage);
              }
              break;
            case FIRST_QUESTION:
              stateEntity = stateMachineRepository.findByUserID(user).get();
              stateEntity.setFirstAnswerAboutRules(teamJoinPayload.getEvent().getText());
              stateMachineRepository.save(stateEntity);
              machine.sendEvent(QUESTION_SECOND);
              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              break;
            case SECOND_QUESTION:
              stateEntity = stateMachineRepository.findByUserID(user).get();
              stateEntity.setSecondAnswerAboutRules(teamJoinPayload.getEvent().getText());
              stateMachineRepository.save(stateEntity);
              machine.sendEvent(QUESTION_THIRD);
              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              break;
            case THIRD_QUESTION:
              stateEntity = stateMachineRepository.findByUserID(user).get();
              stateEntity.setThirdAnswerAboutRules(teamJoinPayload.getEvent().getText());
              stateMachineRepository.save(stateEntity);
              machine.sendEvent(CHANNELS_INFORMATION);
              machine.sendEvent(Event.AGREE_LICENSE);
              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              break;
            case AGREED_LICENSE:
              machine.getExtendedState().getVariables()
                      .put("gitNick", teamJoinPayload.getEvent().getText());
              machine.sendEvent(Event.LOGIN_CONFIRMATION);
              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              break;
            case CHECK_LOGIN:
              if (teamJoinPayload.getEvent().getText().equals("yes")) {
                machine.sendEvent(Event.ADD_GIT_NAME);
                machine.sendEvent(Event.GET_THE_FIRST_TASK);
                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              } else if (teamJoinPayload.getEvent().getText().equals("no")) {
                machine.sendEvent(DID_NOT_PASS_VERIFICATION_GIT_LOGIN);
                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              } else {
                slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser(),
                        notThatMessage);
              }
              break;
            default:
              slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser(), defaultMessage);
              break;
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
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