package com.community.tools.util.statemachie.actions.configs.tasks;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.configs.ActionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import static com.community.tools.util.statemachie.Event.LAST_TASK;
import static com.community.tools.util.statemachie.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachie.State.CONGRATS_LAST_TASK;

public class LastTaskActionConfig implements ActionConfig {

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

  @Override
  public ExternalTransitionConfigurer<State, Event> configure(ExternalTransitionConfigurer<State, Event> transitions) throws Exception {
   return transitions
        .and()
        .withExternal()
        .source(CHECK_FOR_NEW_TASK)
        .target(CONGRATS_LAST_TASK)
        .event(LAST_TASK)
        .guard(lastTaskGuard)
        .action(lastTaskAction, errorAction);
  }
}
