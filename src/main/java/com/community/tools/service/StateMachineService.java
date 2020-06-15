package com.community.tools.service;

import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import static com.community.tools.util.statemachie.Event.*;
import static com.community.tools.util.statemachie.State.*;

@RequiredArgsConstructor
@Service
public class StateMachineService {
    @Autowired
    private StateMachineRepository stateMachineRepository;

    @Value("${welcome}")
    private String welcome;
    @Value("${checkNickName}")
    private String checkNickName;
    @Value("${congratsAvailableNick}")
    private String congratsAvailableNick;
    @Value("${getFirstTask}")
    private String getFirstTask;
    @Value("${failedCheckNickName}")
    private String failedCheckNickName;
    @Value("${doNotUnderstandWhatTodo}")
    private String doNotUnderstandWhatTodo;
    @Value("${agreeMessage}")
    private String agreeMessage;
    @Value("${addGitName}")
    private String addGitName;
    @Value("${noOneCase}")
    private String noOneCase;
    @Value("${notThatMessage}")
    private String notThatMessage;
    @Autowired
    private StateMachineFactory<State, Event> factory;
    @Autowired
    private StateMachinePersister<State, Event, String> persister;

    private final GitHubService gitHubService;
    private final SlackService slackService;

    public void agreeForGitHubNickName(String nickName, String userId) throws Exception {
        String user = slackService.getUserById(userId);

        StateMachine<State, Event> machine = restoreMachine(userId);

        if (machine.getState().getId() == AGREED_LICENSE) {
            slackService.sendPrivateMessage(user,
                    checkNickName + nickName);

            boolean nicknameMatch = gitHubService.getGitHubAllUsers().stream()
                    .anyMatch(e -> e.getLogin().equals(nickName));
            if (nicknameMatch) {
                 StateEntity stateEntity = new StateEntity();
                 stateEntity.setUserID("123123");
                 stateEntity.setGitName(nickName);
                stateMachineRepository.save(stateEntity);
                if (stateMachineRepository.findByUserID(userId).get().getGitName() != nickName){
                    slackService.sendPrivateMessage(user, "we got some problems, git nick name = " + stateEntity.getGitName());
                }
                slackService.sendPrivateMessage(user, congratsAvailableNick);
                machine.sendEvent(ADD_GIT_NAME);

                slackService.sendBlocksMessage(user, getFirstTask);
                machine.sendEvent(GET_THE_FIRST_TASK);
                persistMachine(machine, userId);

            } else {
                slackService.sendPrivateMessage(user, failedCheckNickName);
            }

        } else {
            slackService.sendPrivateMessage(user, doNotUnderstandWhatTodo);

        }
    }

    public void checkActionsFromButton(String action, String userId) throws Exception {
        StateMachine<State, Event> machine = restoreMachine(userId);
        String user = slackService.getUserById(userId);
        switch (action) {
            case "AGREE_LICENSE":

                if (machine.getState().getId() == NEW_USER) {
                    machine.sendEvent(AGREE_LICENSE);
                    persistMachine(machine, userId);
                    slackService.sendBlocksMessage(user, addGitName);
                } else {
                    slackService.sendBlocksMessage(user, notThatMessage);
                }
                break;
            case "theEnd":

                if (machine.getState().getId() == GOT_THE_FIRST_TASK) {
                    slackService
                            .sendPrivateMessage(user, "that was the end, congrats");
                } else {
                    slackService.sendBlocksMessage(user, notThatMessage);
                }
                break;
            default:
                slackService.sendBlocksMessage(user, noOneCase);

        }
    }

    public StateMachine<State, Event> restoreMachine(String id) throws Exception {
        StateMachine<State, Event> machine = factory.getStateMachine();
        machine.start();
        persister.restore(machine, id);
        return machine;
    }

    public void persistMachine(StateMachine<State, Event> machine, String id) throws Exception {
        persister.persist(machine, id);
    }

    public void persistMachineForNewUser(String id) throws Exception {
        StateMachine<State, Event> machine = factory.getStateMachine();
        machine.start();
        persister.persist(machine, id);
    }
}
