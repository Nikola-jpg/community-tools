package com.community.tools.util.statemachie.actions;

import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.methods.SlackApiException;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.io.IOException;


public class VerificationLoginAction implements Action<State, Event> {

  @Autowired
  private SlackService slackService;
  @Autowired
  private GitHubConnectService gitHubConnectService;
  @Autowired
  private GitHubService gitHubService;

  @Override
  public void execute(StateContext<State, Event> context) {
    String user = context.getExtendedState().getVariables().get("id").toString();
    String nickname = context.getExtendedState().getVariables().get("gitNick").toString();
    GHUser userGitLogin;

    try {
      userGitLogin = gitHubService.getUserByLoginInGitHub(nickname);
      slackService.sendPrivateMessage(slackService.getUserById(user),
          "Please tell me is that you? (yes/no) \n" + userGitLogin.getHtmlUrl().toString());
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
