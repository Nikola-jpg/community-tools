package com.community.tools.util.statemachie.actions;

import com.community.tools.controller.GitHubController;
import com.community.tools.controller.GitSlackUsersController;
import com.community.tools.controller.ServletConfig;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.github.AddMentorService;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubHookServlet;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.github.seratch.jslack.api.methods.SlackApiException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHUser;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "/application-test.properties")
@Sql(value = "/test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class HideGuardTest {

  @Autowired
  private HideGuard hideGuard;
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
  @Mock
  private ExtendedState extendedState;
  @Mock
  private GHUser user;

  @Before
  public void setUp() throws Exception {
    Field slackService = HideGuard.class.getDeclaredField("slackService");
    slackService.setAccessible(true);
    slackService.set(hideGuard, slackSer);

    Field repoService = HideGuard.class.getDeclaredField("gitHubService");
    repoService.setAccessible(true);
    repoService.set(hideGuard, gitHubService);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void checkingIfThereIsALoginInTheGithubAndReturnTrue() throws IOException, SlackApiException {
    Map<Object, Object> mockData = new HashMap<>();
    mockData.put("id", "U0191K2V20K");
    mockData.put("gitNick", "likeRewca");

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);

    when(slackSer.getUserById("U0191K2V20K")).thenReturn("Горб Юра");
    when(slackSer.sendPrivateMessage("Горб Юра",
        "Okay! Let me check your nick, " + mockData.get("gitNick"))).thenReturn("");

    when(gitHubService.getUserByLoginInGitHub("likeRewca")).thenReturn(user);
    when(user.getLogin()).thenReturn("likeRewca");

    assertTrue(hideGuard.evaluate(stateContext));

    verify(stateContext, times(2)).getExtendedState();
    verify(slackSer, times(1)).getUserById("U0191K2V20K");
    verify(slackSer, times(1)).
        sendPrivateMessage("Горб Юра", "Okay! Let me check your nick, " + mockData.get("gitNick"));
    verify(gitHubService, times(1)).getUserByLoginInGitHub("likeRewca");
  }

  @Test
  public void checkWhenThereIsNoLoginInTheGithub() throws IOException, SlackApiException {
    Map<Object, Object> mockData = new HashMap<>();
    mockData.put("id", "U0191K2V20K");
    mockData.put("gitNick", "likeRewca");

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);

    when(slackSer.getUserById("U0191K2V20K")).thenReturn("Горб Юра");
    when(slackSer.sendPrivateMessage("Горб Юра",
        "Okay! Let me check your nick, " + mockData.get("gitNick"))).thenReturn("");

    when(gitHubService.getUserByLoginInGitHub("likeRewca")).thenThrow(GHFileNotFoundException.class);

    when(slackSer.sendPrivateMessage("Горб Юра",
        "Sry but looks like you are not registered on Github :worried:")).thenReturn("");

    assertFalse(hideGuard.evaluate(stateContext));

    verify(stateContext, times(2)).getExtendedState();
    verify(slackSer, times(2)).getUserById("U0191K2V20K");
    verify(slackSer, times(1)).
        sendPrivateMessage("Горб Юра", "Okay! Let me check your nick, " + mockData.get("gitNick"));
    verify(slackSer, times(1)).sendPrivateMessage("Горб Юра",
        "Sry but looks like you are not registered on Github :worried:");
    verify(gitHubService, times(1)).getUserByLoginInGitHub("likeRewca");
  }
}