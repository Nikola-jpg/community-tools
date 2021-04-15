package com.community.tools.util.statemachie.actions.transitions.configs.information;

import static com.community.tools.util.statemachie.Event.CHANNELS_INFORMATION;
import static com.community.tools.util.statemachie.State.INFORMATION_CHANNELS;
import static com.community.tools.util.statemachie.State.THIRD_QUESTION;

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
public class ChannelInformationActionTransition implements Transition {

  @Value("${messageAboutSeveralInfoChannel}")
  private String messageAboutSeveralInfoChannel;

  @Autowired
  private SlackService slackService;

  @Autowired
  private Action<State, Event> errorAction;

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    slackService.sendBlocksMessage(slackService.getUserById(user), messageAboutSeveralInfoChannel);
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(THIRD_QUESTION)
        .target(INFORMATION_CHANNELS)
        .event(CHANNELS_INFORMATION)
        .action(this, errorAction);
  }
}
