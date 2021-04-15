package com.community.tools.util.statemachie.actions.transitions.configs.verifications;

import static com.community.tools.util.statemachie.Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static com.community.tools.util.statemachie.State.CHECK_LOGIN;

import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class DidNotPassVerificationGitLoginTrans implements Transition {

  @Autowired
  private Action<State, Event> errorAction;
  @Value("${answeredNoDuringVerification}")
  private String answeredNoDuringVerification;
  @Autowired
  private SlackService slackService;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(CHECK_LOGIN)
        .target(AGREED_LICENSE)
        .event(DID_NOT_PASS_VERIFICATION_GIT_LOGIN)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    slackService.sendPrivateMessage(slackService.getUserById(user),
        answeredNoDuringVerification);
  }
}
