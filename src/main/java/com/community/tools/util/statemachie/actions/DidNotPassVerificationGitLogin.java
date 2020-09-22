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

public class DidNotPassVerificationGitLogin implements Action<State, Event> {

  @Autowired
  private SlackService slackService;
  @Autowired
  private GitHubConnectService gitHubConnectService;
  @Autowired
  private GitHubService gitHubService;

  @Override
  public void execute(StateContext<State, Event> context) {
    String user = context.getExtendedState().getVariables().get("id").toString();
    try {
      slackService.sendPrivateMessage(slackService.getUserById(user),
          "Lets try again! \nWrite your nike again.");
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
