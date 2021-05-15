package com.community.tools.util.statemachie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
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
import org.mockito.ArgumentCaptor;
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

  @Value("${firstQuestion}")
  private String firstQuestion;

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
  void firstQuestionActionTest() {
    String id = "U01QY6GRZ0X";
    User stateEntity = new User();
    stateEntity.setUserID(id);
    stateMachineRepository.save(stateEntity);
    stateMachineService.persistMachineForNewUser(id);
    StateMachine<State, Event> machine = stateMachineService
        .restoreMachine(id);

    when(slackService.getUserById(id)).thenReturn("Some User");
    when(slackService.sendBlocksMessage("Some User", firstQuestion)).thenReturn("");

    machine.sendEvent(Event.QUESTION_FIRST);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);

    verify(slackService).sendBlocksMessage(captor.capture(), captor2.capture());
    assertEquals("Some User", captor.getValue());
    assertEquals(firstQuestion, captor2.getValue());
  }
}
