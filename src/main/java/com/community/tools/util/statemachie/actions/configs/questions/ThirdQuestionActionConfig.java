package com.community.tools.util.statemachie.actions.configs.questions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

import static com.community.tools.util.statemachie.Event.QUESTION_THIRD;
import static com.community.tools.util.statemachie.State.SECOND_QUESTION;
import static com.community.tools.util.statemachie.State.THIRD_QUESTION;

@Component
public class ThirdQuestionActionConfig {

  private Action<State, Event> thirdQuestionAction;
  private Action<State, Event> errorAction;

  @Autowired
  public ThirdQuestionActionConfig(Action<State, Event> thirdQuestionAction,
                                   Action<State, Event> errorAction) {
    this.thirdQuestionAction = thirdQuestionAction;
    this.errorAction = errorAction;
  }

  public ExternalTransitionConfigurer<State, Event> configure(ExternalTransitionConfigurer<State, Event> transitions) throws Exception {
    return transitions
        .and()
        .withExternal()
        .source(SECOND_QUESTION)
        .target(THIRD_QUESTION)
        .event(QUESTION_THIRD)
        .action(thirdQuestionAction, errorAction);
  }
}
