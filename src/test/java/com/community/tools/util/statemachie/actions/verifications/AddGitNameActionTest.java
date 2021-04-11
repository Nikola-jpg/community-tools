package com.community.tools.util.statemachie.actions.verifications;

import com.community.tools.model.User;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackHandlerService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddGitNameActionTest {

  @InjectMocks
  private AddGitNameAction addGitNameAction;
  @Mock
  private StateMachineRepository repository;
  @Mock
  private StateContext<State, Event> stateContext;
  @Mock
  private GitHubConnectService gitHubConnectService;
  @Mock
  private GitHubService gitHubService;
  @Mock
  private SlackService slackSer;
  @Mock
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

  /**
   * This method init fields in the AddGitNameAction.
   * @throws Exception Exception
   */
  @BeforeAll
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

    ReflectionTestUtils.setField(addGitNameAction, "congratsAvailableNick",
            "Hurray! Your nick is available. Nice to meet you :smile:");
    ReflectionTestUtils.setField(addGitNameAction, "channel", "test_3");
  }

  @Test
  public void executeTest() throws Exception {
    Map<Object, Object> mockData = new HashMap<>();
    mockData.put("id", "U0191K2V20K");
    mockData.put("gitNick", "likeRewca");

    Set<GHTeam> mockSet = new HashSet<>();
    mockSet.add(team);

    User entity = new User();

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
    verify(stateContext, times(4)).getExtendedState();
    verify(gitHubService, times(2)).getUserByLoginInGitHub("likeRewca");
    verify(gitHubConnectService, times(2)).getGitHubRepository();
    verify(slackSer, times(5)).getUserById("U0191K2V20K");
    verify(slackSer, times(2))
            .sendPrivateMessage("Горб Юра",
                    "Hurray! Your nick is available. Nice to meet you :smile:");
    verify(slackSer, times(2)).sendMessageToConversation(anyString(), anyString());
  }


  @SneakyThrows
  @Test
  public void shouldGetExceptionWhenAddingToRole() throws IOException {
    Map<Object, Object> mockData = new HashMap<>();
    mockData.put("id", "U0191K2V20K");
    mockData.put("gitNick", "likeRewca");

    Set<GHTeam> mockSet = new HashSet<>();
    mockSet.add(team);

    User entity = new User();

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
            "Something went wrong when adding to role. You need to contact the admin!"))
            .thenReturn("");

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
