package com.community.tools.util.statemachie.actions;

import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubHookServlet;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackHandlerService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.model.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "/application-test.properties")
@Sql(value = "/test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AddGitNameActionTest {

  @Autowired
  private AddGitNameAction addGitNameAction;
  @MockBean
  private StateMachineRepository repository;
  @MockBean
  private StateContext<State, Event> stateContext;
  @MockBean
  private GitHubConnectService gitHubConnectService;
  @MockBean
  private GitHubService gitHubService;
  @MockBean
  private SlackService slackSer;
  @MockBean
  private GitHubHookServlet gitHubHookServlet;
  @MockBean
  private SlackHandlerService slackHandlerService;
  @Mock
  private StateMachine<State, Event> machine;
  @Mock
  private ExtendedState extendedState;
  @Mock
  private GHUser user;
  @Mock
  private GHTeam team;
  @Mock
  private GHRepository ghRepository;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    Field repoField = AddGitNameAction.class.getDeclaredField("gitHubConnectService");
    repoField.setAccessible(true);
    repoField.set(addGitNameAction, gitHubConnectService);

    Field repoService = AddGitNameAction.class.getDeclaredField("gitHubService");
    repoService.setAccessible(true);
    repoService.set(addGitNameAction, gitHubService);

    Field slackService = AddGitNameAction.class.getDeclaredField("slackService");
    slackService.setAccessible(true);
    slackService.set(addGitNameAction, slackSer);

  }

  @Test
  public void executeTest() throws Exception {
    Map<Object, Object> mockData = new HashMap<>();
    mockData.put("id", "U0191K2V20K");
    mockData.put("gitNick", "likeRewca");

    Set<GHTeam> mockSet = new HashSet<>();
    mockSet.add(team);

    StateEntity entity = new StateEntity();

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);

    when(repository.findByUserID("U0191K2V20K")).thenReturn(Optional.of(entity));

    when(gitHubService.getUserByLoginInGitHub("likeRewca")).thenReturn(user);
    when(gitHubConnectService.getGitHubRepository()).thenReturn(ghRepository);
    when(ghRepository.getTeams()).thenReturn(mockSet);
    when(team.getName()).thenReturn("trainees");
    doNothing().when(team).add(user);

    when(slackSer.getUserById("U0191K2V20K")).thenReturn("Горб Юра");
    when(slackSer.sendPrivateMessage("Горб Юра",
        "Hurray! Your nick is available. Nice to meet you :smile:")).thenReturn("");
    when(slackSer.sendMessageToConversation(anyString(), anyString())).thenReturn("");

    addGitNameAction.execute(stateContext);
    verify(stateContext, times(2)).getExtendedState();
    verify(gitHubService, times(1)).getUserByLoginInGitHub("likeRewca");
    verify(gitHubConnectService, times(1)).getGitHubRepository();
    verify(slackSer, times(2)).getUserById("U0191K2V20K");
    verify(slackSer, times(1))
        .sendPrivateMessage("Горб Юра",
            "Hurray! Your nick is available. Nice to meet you :smile:");
    verify(slackSer, times(1)).sendMessageToConversation(anyString(), anyString());
  }


  @Test
  public void shouldGetExceptionWhenAddingToRole() throws IOException {
    Map<Object, Object> mockData = new HashMap<>();
    mockData.put("id", "U0191K2V20K");
    mockData.put("gitNick", "likeRewca");

    Set<GHTeam> mockSet = new HashSet<>();
    mockSet.add(team);

    StateEntity entity = new StateEntity();

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);

    when(repository.findByUserID("U0191K2V20K")).thenReturn(Optional.of(entity));

    when(gitHubService.getUserByLoginInGitHub("likeRewca")).thenReturn(user);
    when(gitHubConnectService.getGitHubRepository()).thenReturn(ghRepository);
    when(ghRepository.getTeams()).thenReturn(mockSet);
    when(team.getName()).thenReturn("trainees");
    doThrow(IOException.class).when(team).add(user);

    when(slackSer.getUserById("U0191K2V20K")).thenReturn("Горб Юра");
    when(slackSer.sendPrivateMessage("Горб Юра",
        "Something went wrong when adding to role. You need to contact the admin!")).thenReturn("");

    addGitNameAction.execute(stateContext);

    verify(stateContext, times(2)).getExtendedState();
    verify(gitHubService, times(1)).getUserByLoginInGitHub("likeRewca");
    verify(gitHubConnectService, times(1)).getGitHubRepository();
    verify(slackSer, times(3)).getUserById("U0191K2V20K");
    verify(slackSer, times(1))
        .sendPrivateMessage("Горб Юра",
            "Hurray! Your nick is available. Nice to meet you :smile:");
    verify(slackSer, times(1))
        .sendPrivateMessage("Горб Юра",
            "Something went wrong when adding to role. You need to contact the admin!");
    verify(slackSer, times(1)).sendMessageToConversation(anyString(), anyString());
  }
}
