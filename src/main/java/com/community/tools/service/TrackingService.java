package com.community.tools.service;

import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.service.payload.SinglePayload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingService {

  private final MessageService messageService;

  private final StateMachineService stateMachineService;

  private final StateMachineRepository stateMachineRepository;

  @Autowired
  private MessagesToPlatform messagesToPlatform;

  /**
   * Method to start the event by state.
   *
   * @param messageFromUser message from user
   * @param userId user id
   * @throws Exception Exception
   */
  public void doAction(String messageFromUser, String userId) throws Exception {

    StateMachine<State, Event> machine = stateMachineService.restoreMachine(userId);
    String userForQuestion = machine.getExtendedState().getVariables().get("id").toString();

    String message = Messages.DEFAULT_MESSAGE;
    Event event = null;
    Payload payload = null;

    switch (machine.getState().getId()) {
      case NEW_USER:
        if (messageFromUser.equalsIgnoreCase("ready")) {
          payload = new SinglePayload(userId);
          event = Event.QUESTION_FIRST;
        } else {
          message = Messages.NOT_THAT_MESSAGE;
        }
        break;
      case FIRST_QUESTION:
        payload = new QuestionPayload(userId, messageFromUser, userForQuestion);
        event = Event.QUESTION_SECOND;
        break;
      case SECOND_QUESTION:
        payload = new QuestionPayload(userId, messageFromUser, userForQuestion);
        event = Event.QUESTION_THIRD;
        break;
      case THIRD_QUESTION:
        payload = new QuestionPayload(userId, messageFromUser, userForQuestion);
        event = Event.CONSENT_TO_INFORMATION;
        break;
      case AGREED_LICENSE:
        String gitNick = messageFromUser;
        payload = new VerificationPayload(userId, gitNick);
        event = Event.LOGIN_CONFIRMATION;
        break;
      case CHECK_LOGIN:
        if (messageFromUser.equalsIgnoreCase("yes")) {
          event = Event.ADD_GIT_NAME_AND_FIRST_TASK;
        } else if (messageFromUser.equalsIgnoreCase("no")) {
          event = Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
        } else {
          message = Messages.NOT_THAT_MESSAGE;
        }
        payload = (VerificationPayload) machine.getExtendedState().getVariables()
            .get("dataPayload");
        break;
      default:
        event = null;
        payload = null;
    }
    if (event == null) {
      messageService.sendPrivateMessage(messageService.getUserById(userId), message);
    } else {
      machine.getExtendedState().getVariables().put("dataPayload", payload);
      machine.sendEvent(event);
      stateMachineService.persistMachine(machine, payload.getId());
    }
  }

  /**
   * Reset User with userId.
   *
   * @param userId platform user id
   * @throws Exception Exception
   */
  public void resetUser(String userId) throws Exception {

    User stateEntity = new User();
    stateEntity.setUserID(userId);
    String userName = messageService.getUserById(userId);
    stateMachineRepository.save(stateEntity);
    stateMachineService.persistMachineForNewUser(userId);

    messageService.sendPrivateMessage(userName,
        Messages.WELCOME);
    messageService
        .sendBlocksMessage(userName, messagesToPlatform.messageAboutRules);
  }
}
