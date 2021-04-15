package com.community.tools.util.statemachie.actions.transitions.configs.verifications;

import static com.community.tools.util.statemachie.Event.AGREE_LICENSE;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static com.community.tools.util.statemachie.State.INFORMATION_CHANNELS;

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
public class AgreeLicenseActionTransition implements Transition {

  @Value("${addGitName}")
  private String addGitName;

  @Autowired
  private SlackService slackService;

  @Autowired
  private Action<State, Event> errorAction;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(INFORMATION_CHANNELS)
        .target(AGREED_LICENSE)
        .event(AGREE_LICENSE)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    slackService.sendBlocksMessage(slackService.getUserById(user), addGitName);
  }
}
