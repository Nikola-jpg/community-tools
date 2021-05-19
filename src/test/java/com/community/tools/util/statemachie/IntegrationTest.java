package com.community.tools.util.statemachie;

import static com.community.tools.util.statemachie.State.ADDED_GIT;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static com.community.tools.util.statemachie.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachie.State.CHECK_LOGIN;
import static com.community.tools.util.statemachie.State.FIRST_QUESTION;
import static com.community.tools.util.statemachie.State.GOT_THE_FIRST_TASK;
import static com.community.tools.util.statemachie.State.INFORMATION_CHANNELS;
import static com.community.tools.util.statemachie.State.NEW_USER;
import static com.community.tools.util.statemachie.State.SECOND_QUESTION;
import static com.community.tools.util.statemachie.State.THIRD_QUESTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class IntegrationTest {

  private static final String USER_ID = "U01QY6GRZ0X";
  private static final String USER_NAME = "Some User";

  @Value("${firstQuestion}")
  private String firstQuestion;

  @Value("${secondQuestion}")
  private String secondQuestion;

  @Value("${thirdQuestion}")
  private String thirdQuestion;

  @Value("${messageAboutSeveralInfoChannel}")
  private String messageAboutSeveralInfoChannel;

  @Value("${addGitName}")
  private String addGitName;

  @Value("${askAboutProfile}")
  private String askAboutProfile;

  @Value("${congratsAvailableNick}")
  private String congratsAvailableNick;

  @Value("${getFirstTask}")
  private String getFirstTask;

  @Value("${answeredNoDuringVerification}")
  private String answeredNoDuringVerification;

  @Value("${lastTask}")
  private String lastTask;

  @Value("${generalInformationChannel}")
  private String channel;

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

  @Captor
  private ArgumentCaptor<String> firstArg;

  @Captor
  private ArgumentCaptor<String> secondArg;

  @Captor
  private ArgumentCaptor<GHUser> ghUserCaptor;

  private ExtendedState extendedState;

  private StateMachine<State, Event> machine;

  @SneakyThrows
  @BeforeEach
  void setUp() {
    machine = stateMachineService
        .restoreMachine(USER_ID);
    if (machine == null) {
      User stateEntity = new User();
      stateEntity.setUserID(USER_ID);
      stateMachineRepository.save(stateEntity);
      stateMachineService.restoreMachine(USER_ID);
    }
    machine.getExtendedState().getVariables()
        .put("gitNick", USER_NAME);
    machine.getExtendedState().getVariables()
        .put("id", USER_ID);
  }

  @SneakyThrows
  @Test
  void firstQuestionActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(NEW_USER,
            null, null, extendedState)));

    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendBlocksMessage(USER_NAME, firstQuestion)).thenReturn("");

    machine.sendEvent(Event.QUESTION_FIRST);

    verify(slackService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(firstQuestion, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void secondQuestionActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(FIRST_QUESTION,
            null, null, extendedState)));

    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendBlocksMessage(USER_NAME, secondQuestion)).thenReturn("");

    machine.sendEvent(Event.QUESTION_SECOND);

    verify(slackService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(secondQuestion, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void thirdQuestionActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(SECOND_QUESTION,
            null, null, extendedState)));

    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendBlocksMessage(USER_NAME, thirdQuestion)).thenReturn("");

    machine.sendEvent(Event.QUESTION_THIRD);

    verify(slackService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(thirdQuestion, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void channelInformationActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(THIRD_QUESTION,
            null, null, extendedState)));

    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendBlocksMessage(USER_NAME, messageAboutSeveralInfoChannel)).thenReturn("");

    machine.sendEvent(Event.CHANNELS_INFORMATION);

    verify(slackService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(messageAboutSeveralInfoChannel, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void agreeLicenseActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(INFORMATION_CHANNELS,
            null, null, extendedState)));

    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendBlocksMessage(USER_NAME, addGitName)).thenReturn("");

    machine.sendEvent(Event.AGREE_LICENSE);

    verify(slackService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(addGitName, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void verificationLoginActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(AGREED_LICENSE,
            null, null, extendedState)));

    when(gitHubService.getUserByLoginInGitHub(USER_NAME)).thenReturn(user);
    URL url = new URL("http://www.some.com/");
    when(user.getHtmlUrl()).thenReturn(url);
    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendPrivateMessage(USER_NAME, askAboutProfile + "\n" + url)).thenReturn("");
    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);

    machine.sendEvent(Event.LOGIN_CONFIRMATION);

    verify(user, times(1)).getHtmlUrl();
    verify(gitHubService, times(1)).getUserByLoginInGitHub(firstArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    verify(slackService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendPrivateMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(askAboutProfile + "\n" + url, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void addGitNameActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(CHECK_LOGIN,
            null, null, extendedState)));

    Set<GHTeam> mockSet = new HashSet<>();
    mockSet.add(team);
    when(gitHubService.getUserByLoginInGitHub(USER_NAME)).thenReturn(user);
    when(gitHubConnectService.getGitHubRepository()).thenReturn(ghRepository);
    when(ghRepository.getTeams()).thenReturn(mockSet);
    when(team.getName()).thenReturn("trainees");
    doNothing().when(team).add(user);
    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendPrivateMessage(USER_NAME, congratsAvailableNick)).thenReturn("");
    when(slackService.sendMessageToConversation(channel, "")).thenReturn("");

    machine.sendEvent(Event.ADD_GIT_NAME);

    verify(gitHubService, times(1)).getUserByLoginInGitHub(firstArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    verify(gitHubConnectService, times(1)).getGitHubRepository();
    verify(ghRepository, times(1)).getTeams();
    verify(team, times(1)).getName();
    verify(team, times(1)).add(ghUserCaptor.capture());
    assertEquals(user, ghUserCaptor.getValue());
    verify(slackService, times(2)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendPrivateMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(congratsAvailableNick, secondArg.getValue());
    verify(slackService, times(1)).sendMessageToConversation(firstArg.capture(), anyString());
    assertEquals(channel, firstArg.getValue());
  }

  @SneakyThrows
  @Test
  void getTheFirstTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(ADDED_GIT,
            null, null, extendedState)));

    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendBlocksMessage(USER_NAME, getFirstTask)).thenReturn("");

    machine.sendEvent(Event.GET_THE_FIRST_TASK);

    verify(slackService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(getFirstTask, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void didNotPassVerificationLoginTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(CHECK_LOGIN,
            null, null, extendedState)));

    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendPrivateMessage(USER_NAME, answeredNoDuringVerification)).thenReturn("");

    machine.sendEvent(Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN);

    verify(slackService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendPrivateMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(answeredNoDuringVerification, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void checkForNewTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(GOT_THE_FIRST_TASK,
            null, null, extendedState)));
    machine.getExtendedState().getVariables()
        .put("taskNumber", 0);

    String taskMessage =
        "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": "
            + "\"Here is your next "
            + "<https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/"
            + "checkstyle" + "|TASK>.\"}}]";
    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendBlocksMessage(USER_NAME, taskMessage)).thenReturn("");

    machine.sendEvent(Event.GET_THE_NEW_TASK);

    verify(slackService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(taskMessage, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void changeTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(CHECK_FOR_NEW_TASK,
            null, null, extendedState)));
    machine.getExtendedState().getVariables()
        .put("taskNumber", 0);

    machine.sendEvent(Event.CHANGE_TASK);

    assertEquals(1, machine.getExtendedState().getVariables().get("taskNumber"));
  }

  @SneakyThrows
  @Test
  void lastTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(CHECK_FOR_NEW_TASK,
            null, null, extendedState)));
    machine.getExtendedState().getVariables()
        .put("taskNumber", 15);

    when(slackService.getUserById(USER_ID)).thenReturn(USER_NAME);
    when(slackService.sendPrivateMessage(USER_NAME, lastTask)).thenReturn("");

    machine.sendEvent(Event.LAST_TASK);

    verify(slackService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(slackService, times(1)).sendPrivateMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(lastTask, secondArg.getValue());
  }
}
