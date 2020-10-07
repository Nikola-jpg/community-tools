package com.community.tools.service.slack;


import static com.community.tools.util.statemachie.Event.AGREE_LICENSE;

import com.community.tools.service.StateMachineService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.model.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.github.seratch.jslack.app_backend.events.EventsDispatcher;
import com.github.seratch.jslack.app_backend.events.handler.MessageHandler;
import com.github.seratch.jslack.app_backend.events.handler.TeamJoinHandler;
import com.github.seratch.jslack.app_backend.events.payload.MessagePayload;
import com.github.seratch.jslack.app_backend.events.payload.TeamJoinPayload;
import com.github.seratch.jslack.app_backend.events.servlet.SlackEventsApiServlet;
import com.google.gson.JsonParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.community.tools.util.statemachie.Event.*;

@RequiredArgsConstructor
@Component
public class SlackHandlerService {

  @Value("${notThatMessage}")
  private String notThatMessage;
  @Value("${welcome}")
  private String welcome;
  @Value("${idOfSlackBot}")
  private String idOfSlackBot;
  @Value("${agreeMessage}")
  private String agreeMessage;
  @Value("${usersAgreeMessage}")
  private String usersAgreeMessage;
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
        StateEntity stateEntity = new StateEntity();
        stateEntity.setUserID(user);
        stateMachineRepository.save(stateEntity);

        stateMachineService.persistMachineForNewUser(user);
        slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser().getRealName(),
                welcome);
        slackService
                .sendBlocksMessage(teamJoinPayload.getEvent().getUser().getRealName(), agreeMessage);
      } catch (JsonParseException e){
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  };


  private void resetUser(String id) throws Exception {
    String user = slackService.getUserById(id);
    StateEntity stateEntity = new StateEntity();
    stateEntity.setUserID(id);
    stateMachineRepository.save(stateEntity);

    stateMachineService.persistMachineForNewUser(id);
    slackService.sendPrivateMessage(user,
            welcome);
    slackService
            .sendBlocksMessage(user, agreeMessage);
  }

  private MessageHandler messageHandler = new MessageHandler() {
    @Override
    public void handle(MessagePayload teamJoinPayload) {
      
      if (!teamJoinPayload.getEvent().getUser().equals(idOfSlackBot)) {
        try {
          if (teamJoinPayload.getEvent().getText().equals("reset") && testModeSwitcher) {
            resetUser(teamJoinPayload.getEvent().getUser());
          }
          StateMachine<State, Event> machine = stateMachineService
                  .restoreMachine(teamJoinPayload.getEvent().getUser());
          switch (machine.getState().getId()) {
            case CHECK_LOGIN:
              if(teamJoinPayload.getEvent().getText().equals("yes")) {
                machine.sendEvent(Event.ADD_GIT_NAME);
                machine.sendEvent(Event.GET_THE_FIRST_TASK);
                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              } else if (teamJoinPayload.getEvent().getText().equals("no")) {
                machine.sendEvent(DID_NOT_PASS_VERIFICATION_GIT_LOGIN);
              } else {
                slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser(), notThatMessage);
              }
              break;
            case AGREED_LICENSE:
              machine.getExtendedState().getVariables()
                  .put("gitNick", teamJoinPayload.getEvent().getText());
              machine.sendEvent(Event.LOGIN_CONFIRMATION);
              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              break;
            case NEW_USER:
              if (teamJoinPayload.getEvent().getText().equals(usersAgreeMessage)) {
                machine.sendEvent(Event.FIRST_AGREE_MESS);
                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              } else {
                slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser(), notThatMessage);
              }
              break;
            case FIRST_LICENSE_MESS:
              if (teamJoinPayload.getEvent().getText().equals(usersAgreeMessage)) {
                machine.sendEvent(Event.SECOND_AGREE_MESS);
                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              } else {
                slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser(), notThatMessage);
              }
              break;
            case SECOND_LICENSE_MESS:
              if (teamJoinPayload.getEvent().getText().equals(usersAgreeMessage)) {
                machine.sendEvent(AGREE_LICENSE);
                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              } else {
                slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser(), notThatMessage);
              }
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
