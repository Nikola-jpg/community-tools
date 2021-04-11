package com.community.tools.util.statemachie.actions.configs.questions;

import static com.community.tools.util.statemachie.Event.QUESTION_FIRST;
import static com.community.tools.util.statemachie.State.FIRST_QUESTION;
import static com.community.tools.util.statemachie.State.NEW_USER;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;

public class FirstQuestionActionConfig {

  private Action<State, Event> firstQuestionAction;
  private Action<State, Event> errorAction;

  @Autowired
  public FirstQuestionActionConfig(Action<State, Event> firstQuestionAction,
                                   Action<State, Event> errorAction) {
    this.firstQuestionAction = firstQuestionAction;
    this.errorAction = errorAction;
  }

  /**
   * Method for configure transition.
   *
   * @param transitions from StateMachine
   * @return transition for next change
   * @throws Exception chain withExternal throw Exception
   */
  public ExternalTransitionConfigurer<State, Event> configure(
      final StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    return transitions
        .withExternal()
        .source(NEW_USER)
        .target(FIRST_QUESTION)
        .event(QUESTION_FIRST)
        .action(firstQuestionAction, errorAction);
  }
}
