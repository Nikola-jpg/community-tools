package com.community.tools.service.discord;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.model.Messages;

import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.TrackingService;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.service.payload.SinglePayload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("discord")
public class DiscordEventListenerTest {

  @MockBean
  private StateMachineService stateMachineService;

  @Autowired
  private TrackingService trackingService;

  @Autowired
  private MessageService messageService;

  @MockBean
  private StateMachine<State, Event> machine;

  @MockBean
  private org.springframework.statemachine.state.State state;

  @MockBean
  private ExtendedState extendedState;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  @DisplayName("Should on private message received")
  public void shouldOnPrivateMessageReceived() throws Exception {
    String id = "830117510543441930";
    String gitNick = "GrPerets";
    trackingService.resetUser(id);

    Map<Object, Object> mockData = new HashMap<>();

    Payload mockPayload = new VerificationPayload(id, gitNick);
    mockData.put("dataPayload", mockPayload);

    String userForQuestion = gitNick;

    when(stateMachineService.restoreMachine(id)).thenReturn(machine);
    when(machine.getState()).thenReturn(state);
    when(state.getId()).thenReturn(State.CHECK_LOGIN);
    when(machine.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);

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
    verify(stateMachineService, times(7))
        .doAction(machine, payload, stateMachineEvent);
  }
}