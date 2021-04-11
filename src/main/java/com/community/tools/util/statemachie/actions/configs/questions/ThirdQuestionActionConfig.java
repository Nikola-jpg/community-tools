package com.community.tools.util.statemachie.actions.configs.questions;

import static com.community.tools.util.statemachie.Event.QUESTION_THIRD;
import static com.community.tools.util.statemachie.State.SECOND_QUESTION;
import static com.community.tools.util.statemachie.State.THIRD_QUESTION;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.configs.ActionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;

public class ThirdQuestionActionConfig implements ActionConfig {

  private Action<State, Event> thirdQuestionAction;
  private Action<State, Event> errorAction;

  @Autowired
  public ThirdQuestionActionConfig(Action<State, Event> thirdQuestionAction,
                                   Action<State, Event> errorAction) {
    this.thirdQuestionAction = thirdQuestionAction;
    this.errorAction = errorAction;
  }

  @Override
  public ExternalTransitionConfigurer<State, Event> configure(
      ExternalTransitionConfigurer<State, Event> transitions) throws Exception {
    return transitions
        .and()
        .withExternal()
        .source(SECOND_QUESTION)
        .target(THIRD_QUESTION)
        .event(QUESTION_THIRD)
        .action(thirdQuestionAction, errorAction);
  }
}
