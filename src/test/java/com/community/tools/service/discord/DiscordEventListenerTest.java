package com.community.tools.service.discord;

import com.community.tools.model.Messages;

import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.service.payload.SinglePayload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ActiveProfiles("discord")
class DiscordEventListenerTest {

  @Autowired
  private DiscordEventListener discordEventListener;

  @Autowired
  private StateMachineService stateMachineService;

  @Autowired
  @Qualifier("discordService")
  private MessageService messageService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }
  /*
  @Test
  @DisplayName("Should on private message received")
  void shouldOnPrivateMessageReceived() throws Exception {
    String id = "830117510543441930";
    discordEventListener.resetUser("830117510543441930");
    StateMachine<State, Event> machine = stateMachineService
        .restoreMachine(id);

    String userForQuestion = machine.getExtendedState().getVariables().get("id").toString();

    String message = Messages.DEFAULT_MESSAGE;
    Event stateMachineEvent = null;
    Payload payload = null;
    for (int i = 0; i < 7; i++) {
      switch (machine.getState().getId()) {
        case NEW_USER:
          if (true) {
            payload = new SinglePayload(id);
            stateMachineEvent = Event.QUESTION_FIRST;
          } else {
            message = Messages.NOT_THAT_MESSAGE;
          }
          break;
        case FIRST_QUESTION:
          payload = new QuestionPayload(id, "First", userForQuestion);
          stateMachineEvent = Event.QUESTION_SECOND;
          break;
        case SECOND_QUESTION:
          payload = new QuestionPayload(id, "Second", userForQuestion);
          stateMachineEvent = Event.QUESTION_THIRD;
          break;
        case THIRD_QUESTION:
          payload = new QuestionPayload(id, "Third", userForQuestion);
          stateMachineEvent = Event.CONSENT_TO_INFORMATION;
          break;
        case AGREED_LICENSE:
          String gitNick = "nperets";
          payload = new VerificationPayload(id, gitNick);
          stateMachineEvent = Event.LOGIN_CONFIRMATION;
          break;
        case CHECK_LOGIN:
          if ("yes".equals("yes")) {
            stateMachineEvent = Event.ADD_GIT_NAME_AND_FIRST_TASK;
          } else if ("no".equals("no")) {
            stateMachineEvent = Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
          } else {
            message = Messages.NOT_THAT_MESSAGE;
          }
          payload = (VerificationPayload) machine.getExtendedState().getVariables()
              .get("dataPayload");
          break;
        default:
          stateMachineEvent = null;
          payload = null;
      }
      if (stateMachineEvent == null) {
        messageService.sendPrivateMessage(
            messageService.getUserById(id),
            message);
      } else {
        stateMachineService
            .doAction(machine, payload, stateMachineEvent);
      }
    }
  }*/
}