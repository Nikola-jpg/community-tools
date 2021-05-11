package com.community.tools.util.statemachie;

import static org.mockito.Mockito.when;

import com.community.tools.model.User;

import com.community.tools.service.StateMachineService;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
class IntegrationTest {

  @Value("Oops, I'm sorry, but I don't have an answer to your request.")
  private String defaultMessage;

  @Autowired
  private StateMachineService stateMachineService;

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @MockBean
  private SlackService slackService;

  @MockBean
  private GitHubService gitHubService;

  @MockBean
  private GitHubConnectService gitHubConnectService;

  @Mock
  private GHUser user;

  @Mock
  private GHTeam team;

  @Mock
  private GHRepository ghRepository;

  @SneakyThrows
  @Test
  void stateMachineTest() {
    String id = "U01QY6GRZ0X";

    User stateEntity = new User();
    stateEntity.setUserID(id);
    stateMachineRepository.save(stateEntity);
    stateMachineService.persistMachineForNewUser(id);

    StateMachine<State, Event> machine = stateMachineService
        .restoreMachine(id);
    String userServ = machine.getExtendedState().getVariables().get("id").toString();

    for (int i = 0; i < 7; i++) {
      switch (machine.getState().getId()) {
        case NEW_USER:
          machine.sendEvent(Event.QUESTION_FIRST);
          stateMachineService.persistMachine(machine, id);
          break;
        case FIRST_QUESTION:
          stateEntity = stateMachineRepository.findByUserID(userServ).get();
          stateEntity.setFirstAnswerAboutRules("First");
          stateMachineRepository.save(stateEntity);
          machine.sendEvent(Event.QUESTION_SECOND);
          stateMachineService.persistMachine(machine, id);
          break;
        case SECOND_QUESTION:
          stateEntity = stateMachineRepository.findByUserID(userServ).get();
          stateEntity.setSecondAnswerAboutRules("Second");
          stateMachineRepository.save(stateEntity);
          machine.sendEvent(Event.QUESTION_THIRD);
          stateMachineService.persistMachine(machine, id);
          break;
        case THIRD_QUESTION:
          stateEntity = stateMachineRepository.findByUserID(userServ).get();
          stateEntity.setThirdAnswerAboutRules("Third");
          stateMachineRepository.save(stateEntity);
          machine.sendEvent(Event.CHANNELS_INFORMATION);
          machine.sendEvent(Event.AGREE_LICENSE);
          stateMachineService.persistMachine(machine, id);
          break;
        case AGREED_LICENSE:
          machine.getExtendedState().getVariables()
              .put("gitNick", "libenko96");
          when(gitHubService.getUserByLoginInGitHub("libenko96")).thenReturn(user);
          URL u = new URL("http://www.some.com/");
          when(user.getHtmlUrl()).thenReturn(u);
          machine.sendEvent(Event.LOGIN_CONFIRMATION);
          stateMachineService.persistMachine(machine, id);
          break;
        case CHECK_LOGIN:
          if ("yes".equals("yes")) {
            Set<GHTeam> mockSet = new HashSet<>();
            mockSet.add(team);
            when(gitHubService.getUserByLoginInGitHub("libenko96")).thenReturn(user);
            when(gitHubConnectService.getGitHubRepository()).thenReturn(ghRepository);
            when(ghRepository.getTeams()).thenReturn(mockSet);
            when(team.getName()).thenReturn("trainees");
            machine.sendEvent(Event.ADD_GIT_NAME);
            machine.sendEvent(Event.GET_THE_FIRST_TASK);
            stateMachineService.persistMachine(machine, id);
          } else if ("no".equals("no")) {
            machine.sendEvent(Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN);
            stateMachineService.persistMachine(machine, id);
          } else {
            when(slackService.sendPrivateMessage("Илья Либенко",
                "notThatMessage")).thenReturn("");
            slackService.sendPrivateMessage(id,
                "notThatMessage");
          }
          break;
        case ADDED_GIT: {
          machine.sendEvent(Event.GET_THE_FIRST_TASK);
          stateMachineService.persistMachine(machine, id);
          break;
        }
        default:
          when(slackService.sendPrivateMessage("Илья Либенко", defaultMessage)).thenReturn("");
          slackService.sendPrivateMessage("Илья Либенко", defaultMessage);
          break;
      }
    }
  }
}
