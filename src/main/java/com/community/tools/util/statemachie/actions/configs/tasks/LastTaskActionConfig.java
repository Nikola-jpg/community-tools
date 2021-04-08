package com.community.tools.util.statemachie.actions.configs.tasks;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import static com.community.tools.util.statemachie.Event.LAST_TASK;
import static com.community.tools.util.statemachie.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachie.State.CONGRATS_LAST_TASK;

@Component
public class LastTaskActionConfig {

  private Action<State, Event> lastTaskAction;
  private Action<State, Event> errorAction;
  private Guard<State, Event> lastTaskGuard;

  @Autowired
  public LastTaskActionConfig(Action<State, Event> lastTaskAction,
                              Action<State, Event> errorAction, Guard<State, Event> lastTaskGuard) {
    this.lastTaskAction = lastTaskAction;
    this.errorAction = errorAction;
    this.lastTaskGuard = lastTaskGuard;
  }

  public void configure(ExternalTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .and()
        .withExternal()
        .source(CHECK_FOR_NEW_TASK)
        .target(CONGRATS_LAST_TASK)
        .event(LAST_TASK)
        .guard(lastTaskGuard)
        .action(lastTaskAction, errorAction);
  }
}
