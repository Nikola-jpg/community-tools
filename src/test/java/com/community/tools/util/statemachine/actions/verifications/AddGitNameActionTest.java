package com.community.tools.util.statemachine.actions.verifications;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.model.User;
import com.community.tools.service.BlockService;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.service.slack.SlackHandlerService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.transitions.verifications.AddGitNameActionTransition;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddGitNameActionTest {

  @InjectMocks
  private AddGitNameActionTransition addGitNameAction;
  @Mock
  private StateMachineRepository repository;
  @Mock
  private StateContext<State, Event> stateContext;
  @Mock
  private GitHubConnectService gitHubConnectService;
  @Mock
  private GitHubService gitHubService;
  @Mock
  private MessageService messageService;
  @Mock
  private Map<String, MessageService> messageServiceMap;
  @Mock
  private BlockService blockService;
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

  private final String getFirstTask = "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"Hurray! Your nick is available. Nice to meet you :smile:\n\nThis is your first <https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/checkstyle|TASK>. gl\"}}]";

  private final String errorWithAddingGitName = "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"Something went wrong with adding to the team. Please, contact *<https://broscorp-community.slack.com/archives/D01QZ9U2GH5|Liliya Stepanovna>*\"}}]";

  /**
   * This method init fields in the AddGitNameAction.
   *
   * @throws Exception Exception
   */
  @BeforeAll
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    Field repoField = AddGitNameActionTransition.class.getDeclaredField("gitHubConnectService");
    repoField.setAccessible(true);
    repoField.set(addGitNameAction, gitHubConnectService);

    Field repoService = AddGitNameActionTransition.class.getDeclaredField("gitHubService");
    repoService.setAccessible(true);
    repoService.set(addGitNameAction, gitHubService);

    //Field messageService = AddGitNameActionTransition.class.getDeclaredField("messageService");
    //messageService.setAccessible(true);
    //messageService.set(addGitNameAction, this.messageService);

    Field blockService = AddGitNameActionTransition.class.getDeclaredField("blockService");
    blockService.setAccessible(true);
    blockService.set(addGitNameAction, this.blockService);

    Field messageServiceMap = AddGitNameActionTransition.class
        .getDeclaredField("messageServiceMap");
    messageServiceMap.setAccessible(true);
    messageServiceMap.set(addGitNameAction, this.messageServiceMap);

    ReflectionTestUtils.setField(addGitNameAction, "channel", "test_3");
    ReflectionTestUtils.setField(addGitNameAction, "getFirstTask", getFirstTask);
    ReflectionTestUtils
        .setField(addGitNameAction, "errorWithAddingGitName", errorWithAddingGitName);
    ReflectionTestUtils.setField(addGitNameAction, "currentMessageService", "slackService");
  }

  @Test
  public void executeTest() throws Exception {
    Map<Object, Object> mockData = new HashMap<>();

    Payload payload = new VerificationPayload("U0191K2V20K", "likeRewca");
    mockData.put("dataPayload", payload);

    Set<GHTeam> mockSet = new HashSet<>();
    mockSet.add(team);

    User entity = new User();

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);
    when(messageServiceMap.get(anyString())).thenReturn(messageService);
    when(blockService.createBlockMessage(anyString(), any())).thenReturn(getFirstTask);
    when(repository.findByUserID("U0191K2V20K")).thenReturn(Optional.of(entity));

    when(gitHubService.getUserByLoginInGitHub("likeRewca")).thenReturn(user);
    when(gitHubConnectService.getGitHubRepository()).thenReturn(ghRepository);
    when(ghRepository.getTeams()).thenReturn(mockSet);
    when(team.getName()).thenReturn("trainees");
    doNothing().when(team).add(user);

    when(messageService.getUserById("U0191K2V20K")).thenReturn("Горб Юра");
    when(messageService.sendMessageToConversation(anyString(), anyString())).thenReturn("");
    when(messageService.sendBlocksMessage("Горб Юра",
        getFirstTask)).thenReturn("");

    addGitNameAction.execute(stateContext);
    verify(stateContext, times(4)).getExtendedState();
    verify(gitHubService, times(2)).getUserByLoginInGitHub("likeRewca");
    verify(gitHubConnectService, times(2)).getGitHubRepository();
    verify(messageService, times(5)).getUserById("U0191K2V20K");

    verify(messageService, times(2)).sendMessageToConversation(anyString(), anyString());
    verify(messageService, times(2))
        .sendBlocksMessage("Горб Юра",
            getFirstTask);
  }


  @SneakyThrows
  @Test
  public void shouldGetExceptionWhenAddingToRole() throws IOException {
    Map<Object, Object> mockData = new HashMap<>();

    Payload payload = new VerificationPayload("U0191K2V20K", "likeRewca");
    mockData.put("dataPayload", payload);

    Set<GHTeam> mockSet = new HashSet<>();
    mockSet.add(team);

    User entity = new User();

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);
    when(messageServiceMap.get(anyString())).thenReturn(messageService);
    when(blockService.createBlockMessage(eq(errorWithAddingGitName), any()))
        .thenReturn(errorWithAddingGitName);
    when(blockService.createBlockMessage(eq(getFirstTask), any())).thenReturn(getFirstTask);
    when(repository.findByUserID("U0191K2V20K")).thenReturn(Optional.of(entity));

    when(gitHubService.getUserByLoginInGitHub("likeRewca")).thenReturn(user);
    when(gitHubConnectService.getGitHubRepository()).thenReturn(ghRepository);
    when(ghRepository.getTeams()).thenReturn(mockSet);
    when(team.getName()).thenReturn("trainees");
    doThrow(IOException.class).when(team).add(user);

    when(messageService.getUserById("U0191K2V20K")).thenReturn("Горб Юра");
    when(messageService.sendBlocksMessage("Горб Юра",
        errorWithAddingGitName)).thenReturn("");
    when(messageService.sendBlocksMessage("Горб Юра",
        getFirstTask)).thenReturn("");

    addGitNameAction.execute(stateContext);
    verify(stateContext, times(2)).getExtendedState();
    verify(gitHubService, times(1)).getUserByLoginInGitHub("likeRewca");
    verify(gitHubConnectService, times(1)).getGitHubRepository();
    verify(messageService, times(3)).getUserById("U0191K2V20K");
    verify(messageService, times(1))
        .sendBlocksMessage("Горб Юра",
            errorWithAddingGitName);
    verify(messageService, times(1)).sendMessageToConversation(anyString(), anyString());

    verify(messageService, times(1))
        .sendBlocksMessage("Горб Юра",
            getFirstTask);

  }
}
