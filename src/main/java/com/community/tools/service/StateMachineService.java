package com.community.tools.service;

import static com.community.tools.util.statemachine.Event.ADD_GIT_NAME;
import static com.community.tools.util.statemachine.Event.GET_THE_FIRST_TASK;
import static com.community.tools.util.statemachine.Event.QUESTION_FIRST;
import static com.community.tools.util.statemachine.State.AGREED_LICENSE;
import static com.community.tools.util.statemachine.State.GOT_THE_FIRST_TASK;
import static com.community.tools.util.statemachine.State.NEW_USER;

import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.service.discord.MessagesToDiscord;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.slack.MessagesToSlack;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StateMachineService {

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Value("${welcome}")
  private String welcome;
  @Value("${checkNickName}")
  private String checkNickName;

  @Value("${failedCheckNickName}")
  private String failedCheckNickName;
  @Value("${doNotUnderstandWhatTodo}")
  private String doNotUnderstandWhatTodo;

  @Value("${noOneCase}")
  private String noOneCase;
  @Value("${notThatMessage}")
  private String notThatMessage;
  @Autowired
  private StateMachineFactory<State, Event> factory;
  @Autowired
  private StateMachinePersister<State, Event, String> persister;

  private final GitHubService gitHubService;

  @Autowired
  private BlockService blockService;

  @Autowired
  private Map<String, MessageService> messageServiceMap;

  @Value("${currentMessageService}")
  private String currentMessageService;

  /**
   * Selected current message service.
   * @return current message service
   */
  public MessageService getMessageService() {
    return messageServiceMap.get(currentMessageService);
  }


  /**
   * Check Slack`s user and Github login.
   *
   * @param nickName GitHub login
   * @param userId   Slack`s userId
   * @throws Exception Exception
   */
  public void agreeForGitHubNickName(String nickName, String userId) throws Exception {
    String user = getMessageService().getUserById(userId);

    StateMachine<State, Event> machine = restoreMachine(userId);

    if (machine.getState().getId() == AGREED_LICENSE) {
      getMessageService().sendPrivateMessage(user,
          checkNickName + nickName);

      boolean nicknameMatch = gitHubService.getGitHubAllUsers().stream()
          .anyMatch(e -> e.getLogin().equals(nickName));
      if (nicknameMatch) {

        machine.sendEvent(ADD_GIT_NAME);
        machine.sendEvent(GET_THE_FIRST_TASK);
        persistMachine(machine, userId);

        User stateEntity = stateMachineRepository.findByUserID(userId).get();
        stateEntity.setGitName(nickName);
        stateMachineRepository.save(stateEntity);

      } else {
        getMessageService().sendPrivateMessage(user, failedCheckNickName);
      }

    } else {
      getMessageService().sendPrivateMessage(user, doNotUnderstandWhatTodo);

    }
  }

  /**
   * Check action from Slack`s user.
   *
   * @param action action
   * @param userId Slack`s userId
   * @throws Exception Exception
   */
  public void checkActionsFromButton(String action, String userId) throws Exception {
    StateMachine<State, Event> machine = restoreMachine(userId);
    String user = getMessageService().getUserById(userId);
    switch (action) {
      case "AGREE_LICENSE":
        if (machine.getState().getId() == NEW_USER) {
          machine.sendEvent(QUESTION_FIRST);
          persistMachine(machine, userId);
        } else {
          getMessageService().sendBlocksMessage(user, blockService.createBlockMessage(
              MessagesToSlack.NOT_THAT_MESSAGE, MessagesToDiscord.NOT_THAT_MESSAGE));
        }
        break;
      case "theEnd":

        if (machine.getState().getId() == GOT_THE_FIRST_TASK) {
          machine.sendEvent(GET_THE_FIRST_TASK);
          getMessageService()
              .sendPrivateMessage(user, Messages.CONGRATS);
        } else {
          getMessageService().sendBlocksMessage(user, blockService.createBlockMessage(
              MessagesToSlack.NOT_THAT_MESSAGE, MessagesToDiscord.NOT_THAT_MESSAGE));
        }
        break;
      default:
        getMessageService().sendBlocksMessage(user, blockService.createBlockMessage(
            MessagesToSlack.NO_ONE_CASE, MessagesToDiscord.NO_ONE_CASE));

    }
  }

  /**
   * Restore machine by Slack`s userId.
   *
   * @param id Slack`s userId
   * @return StateMachine
   * @throws Exception Exception
   */
  public StateMachine<State, Event> restoreMachine(String id) throws Exception {
    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.start();
    persister.restore(machine, id);
    return machine;
  }

  /**
   * Restore machine by GitHub Login.
   *
   * @param nick GitHub login
   * @return StateMachine
   */
  public StateMachine<State, Event> restoreMachineByNick(String nick) {
    User user = stateMachineRepository.findByGitName(nick).get();
    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.start();
    try {
      persister.restore(machine, user.getUserID());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return machine;
  }

  public String getIdByNick(String nick) {
    return stateMachineRepository.findByGitName(nick).get().getUserID();
  }

  /**
   * Persist machine for User by userId.
   *
   * @param machine StateMachine
   * @param id      Slack`s userId
   */
  public void persistMachine(StateMachine<State, Event> machine, String id) {
    try {
      persister.persist(machine, id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Persist machine for new User by userId.
   *
   * @param id Slack`s userId
   * @throws Exception Exception
   */
  public void persistMachineForNewUser(String id) throws Exception {
    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.getExtendedState().getVariables().put("id", id);
    machine.getExtendedState().getVariables().put("taskNumber", 1);
    machine.getExtendedState().getVariables().put("mentor", "NO_MENTOR");
    machine.start();
    persister.persist(machine, id);
  }

  /**
   * Method to start the action.
   *
   * @param payload - payload that stores data to execute Actions
   * @param event   - event for StateMachine
   */
  public void doAction(StateMachine<State, Event> machine, Payload payload, Event event) {
    machine.getExtendedState().getVariables().put("dataPayload", payload);
    machine.sendEvent(event);
    persistMachine(machine, payload.getId());
  }
}