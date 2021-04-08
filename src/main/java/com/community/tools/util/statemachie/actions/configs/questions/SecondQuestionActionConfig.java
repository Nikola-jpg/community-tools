package com.community.tools.util.statemachie.actions.configs.questions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

import static com.community.tools.util.statemachie.Event.QUESTION_SECOND;
import static com.community.tools.util.statemachie.State.FIRST_QUESTION;
import static com.community.tools.util.statemachie.State.SECOND_QUESTION;

@Component
public class SecondQuestionActionConfig {

  private Action<State, Event> secondQuestionAction;
  private Action<State, Event> errorAction;

  @Autowired
  public SecondQuestionActionConfig(Action<State, Event> secondQuestionAction,
                                    Action<State, Event> errorAction) {
    this.secondQuestionAction = secondQuestionAction;
    this.errorAction = errorAction;
  }

  public ExternalTransitionConfigurer<State, Event> configure(ExternalTransitionConfigurer<State, Event> transitions) throws Exception {
    return transitions
        .and()
        .withExternal()
        .source(FIRST_QUESTION)
        .target(SECOND_QUESTION)
        .event(QUESTION_SECOND)
        .action(secondQuestionAction, errorAction);
  }
}
