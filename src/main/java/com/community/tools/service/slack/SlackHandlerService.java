package com.community.tools.service.slack;

import com.community.tools.model.User;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.payload.AddedGitPayload;
import com.community.tools.service.payload.AgreedLicensePayload;
import com.community.tools.service.payload.CheckLoginPayload;
import com.community.tools.service.payload.NewUserPayload;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.QuestionPayload;
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
   *
   * @param id Slack id
   * @throws Exception Exception
   */
  public void resetUser(String id) throws Exception {

    User stateEntity = new User();
    stateEntity.setUserID(id);
    stateMachineRepository.save(stateEntity);
    stateMachineService.persistMachineForNewUser(id);

    String user = slackService.getUserById(id);
    slackService.sendPrivateMessage(user, welcome);
    slackService.sendBlocksMessage(user, messageAboutRules);
  }

  private MessageHandler messageHandler = new MessageHandler() {
    @Override
    public void handle(MessagePayload teamJoinPayload) {
      User stateEntity;
      MessageEvent messageEvent = teamJoinPayload.getEvent();
      if (!messageEvent.getUser().equals(idOfSlackBot)) {
        try {
          if (messageEvent.getText().equals("reset") && testModeSwitcher) {
            resetUser(messageEvent.getUser());
          }
          StateMachine<State, Event> machine = stateMachineService
              .restoreMachine(messageEvent.getUser());

          String userId = machine.getExtendedState().getVariables().get("id").toString();

          String message = defaultMessage;
          Event event = null;
          Payload payload = null;
          switch (machine.getState().getId()) {
            case NEW_USER:
              if (messageEvent.getText().equals("ready")) {
                payload = new NewUserPayload(
                    Integer.parseInt(userId),
                    messageEvent,
                    Integer.parseInt(
                        machine.getExtendedState().getVariables().get("taskNumber").toString()),
                    machine.getExtendedState().getVariables().get("mentor").toString()
                );
                event = Event.QUESTION_FIRST;
              } else {
                message = notThatMessage;
              }
              break;
            case FIRST_QUESTION:
              payload = new QuestionPayload(Integer.parseInt(userId), messageEvent);
              event = Event.QUESTION_SECOND;
              break;
            case SECOND_QUESTION:
              payload = new QuestionPayload(Integer.parseInt(userId), messageEvent);
              event = Event.QUESTION_THIRD;
              break;
            case THIRD_QUESTION:
              payload = new QuestionPayload(Integer.parseInt(userId), messageEvent);
              event = Event.CHANNELS_INFORMATION;
              break;
            case AGREED_LICENSE:
              payload = new AgreedLicensePayload(
                  Integer.parseInt(userId),
                  messageEvent,
                  messageEvent.getText()
              );
              event = Event.LOGIN_CONFIRMATION;
              break;
            case CHECK_LOGIN:
              if (messageEvent.getText().equals("yes")) {
                event = Event.ADD_GIT_NAME;
              } else if (messageEvent.getText().equals("no")) {
                event = Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
              } else {
                message = notThatMessage;
              }
              payload = new CheckLoginPayload(Integer.parseInt(userId), messageEvent);
              break;
            case ADDED_GIT:
              payload = new AddedGitPayload(Integer.parseInt(userId), messageEvent);
              event = Event.GET_THE_FIRST_TASK;
              break;
          }

          if (event == null) {
            slackService.sendPrivateMessage(
                slackService.getUserById(messageEvent.getUser()),
                message);
          } else {
            stateMachineService
                .doAction(payload, event);
          }

//          switch (machine.getState().getId()) {
//            case NEW_USER:
//              if (teamJoinPayload.getEvent().getText().equals("ready")) {
//                machine.sendEvent(Event.QUESTION_FIRST);
//                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
//              } else {
//                slackService.sendPrivateMessage(
//                    slackService.getUserById(teamJoinPayload.getEvent().getUser()),
//                    notThatMessage);
//              }
//              break;
//            case FIRST_QUESTION:
//              stateEntity = stateMachineRepository.findByUserID(userId).get();
//              stateEntity.setFirstAnswerAboutRules(teamJoinPayload.getEvent().getText());
//              stateMachineRepository.save(stateEntity);
//              machine.sendEvent(Event.QUESTION_SECOND);
//              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
//              break;
//            case SECOND_QUESTION:
//              stateEntity = stateMachineRepository.findByUserID(userId).get();
//              stateEntity.setSecondAnswerAboutRules(teamJoinPayload.getEvent().getText());
//              stateMachineRepository.save(stateEntity);
//              machine.sendEvent(Event.QUESTION_THIRD);
//              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
//              break;
//            case THIRD_QUESTION:
//              stateEntity = stateMachineRepository.findByUserID(userId).get();
//              stateEntity.setThirdAnswerAboutRules(teamJoinPayload.getEvent().getText());
//              stateMachineRepository.save(stateEntity);
//              machine.sendEvent(Event.CHANNELS_INFORMATION);
//              machine.sendEvent(Event.AGREE_LICENSE);
//              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
//              break;
//            case AGREED_LICENSE:
//              machine.getExtendedState().getVariables()
//                  .put("gitNick", teamJoinPayload.getEvent().getText());
//              machine.sendEvent(Event.LOGIN_CONFIRMATION);
//              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
//              break;
//            case CHECK_LOGIN:
//              if (teamJoinPayload.getEvent().getText().equals("yes")) {
//                machine.sendEvent(Event.ADD_GIT_NAME);
//                machine.sendEvent(Event.GET_THE_FIRST_TASK);
//                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
//              } else if (teamJoinPayload.getEvent().getText().equals("no")) {
//                machine.sendEvent(Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN);
//                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
//              } else {
//                slackService.sendPrivateMessage(
//                    slackService.getUserById(teamJoinPayload.getEvent().getUser()),
//                    notThatMessage);
//              }
//              break;
//            case ADDED_GIT:
//              machine.sendEvent(Event.GET_THE_FIRST_TASK);
//              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
//              break;
//            default:
//              slackService.sendPrivateMessage(
//                  slackService.getUserById(teamJoinPayload.getEvent().getUser()),
//                  defaultMessage);
//              break;
//          }
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