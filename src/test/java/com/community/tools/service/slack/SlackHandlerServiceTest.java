package com.community.tools.service.slack;

import static org.junit.jupiter.api.Assertions.*;

import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.service.payload.SinglePayload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "/application-test.properties")
class SlackHandlerServiceTest {

  @Autowired
  private StateMachineService stateMachineService;

  @Autowired
  private SlackHandlerService slackHandlerService;

  @Autowired
  @Qualifier("slackService")
  private MessageService messageService;


  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  public void testStateMachineSlack() throws Exception {
    String id = "U01QY5TJ71V";
  slackHandlerService.resetUser("U01QY5TJ71V");
    StateMachine<State, Event> machine = stateMachineService
        .restoreMachine(id);

    String userForQuestion = machine.getExtendedState().getVariables().get("id").toString();

    String message = Messages.DEFAULT_MESSAGE;
    Event event = null;
    Payload payload = null;
    for (int i = 0; i < 7; i++) {
      switch (machine.getState().getId()) {
        case NEW_USER:
          if (true) {
            payload = new SinglePayload(id);
            event = Event.QUESTION_FIRST;
          } else {
            message = Messages.NOT_THAT_MESSAGE;
          }
          break;
        case FIRST_QUESTION:
          payload = new QuestionPayload(id, "First", userForQuestion);
          event = Event.QUESTION_SECOND;
          break;
        case SECOND_QUESTION:
          payload = new QuestionPayload(id, "Second", userForQuestion);
          event = Event.QUESTION_THIRD;
          break;
        case THIRD_QUESTION:
          payload = new QuestionPayload(id, "Third", userForQuestion);
          event = Event.CONSENT_TO_INFORMATION;
          break;
        case AGREED_LICENSE:
          String gitNick = "GrPerets";
          payload = new VerificationPayload(id, gitNick);
          event = Event.LOGIN_CONFIRMATION;
          break;
        case CHECK_LOGIN:
          if ("yes".equals("yes")) {
            event = Event.ADD_GIT_NAME;
          } else if ("no".equals("no")) {
            event = Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
          } else {
            message = Messages.NOT_THAT_MESSAGE;
          }
          payload = (VerificationPayload) machine.getExtendedState().getVariables()
              .get("dataPayload");
          break;
        case ADDED_GIT:
          payload = new SinglePayload(id);
          event = Event.GET_THE_FIRST_TASK;
          break;
        default:
          event = null;
          payload = null;
      }
      if (event == null) {
        messageService.sendPrivateMessage(
            messageService.getUserById(id),
            message);
      } else {
        stateMachineService
            .doAction(machine, payload, event);
      }
    }
  }

}