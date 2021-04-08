package com.community.tools.util.statemachie.actions.verifications;

import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class DidNotPassVerificationGitLogin implements Action<State, Event> {

  @Value("${answeredNoDuringVerification}")
  private String answeredNoDuringVerification;
  @Autowired
  private SlackService slackService;
  @Autowired
  private GitHubConnectService gitHubConnectService;
  @Autowired
  private GitHubService gitHubService;

  @Override
  public void execute(StateContext<State, Event> context) {
    String user = context.getExtendedState().getVariables().get("id").toString();
    slackService.sendPrivateMessage(slackService.getUserById(user),
        answeredNoDuringVerification);
  }
}
