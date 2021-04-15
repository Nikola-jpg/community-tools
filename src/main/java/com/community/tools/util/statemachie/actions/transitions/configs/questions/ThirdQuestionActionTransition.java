package com.community.tools.util.statemachie.actions.transitions.configs.questions;

import static com.community.tools.util.statemachie.Event.QUESTION_THIRD;
import static com.community.tools.util.statemachie.State.SECOND_QUESTION;
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
public class ThirdQuestionActionTransition implements Transition {

  @Value("${thirdQuestion}")
  private String thirdQuestion;

  @Autowired
  private SlackService slackService;

  @Autowired
  private Action<State, Event> errorAction;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(SECOND_QUESTION)
        .target(THIRD_QUESTION)
        .event(QUESTION_THIRD)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    slackService.sendBlocksMessage(slackService.getUserById(user), thirdQuestion);
  }
}
