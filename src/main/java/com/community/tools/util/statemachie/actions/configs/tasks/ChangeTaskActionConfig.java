package com.community.tools.util.statemachie.actions.configs.tasks;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

import static com.community.tools.util.statemachie.Event.CHANGE_TASK;
import static com.community.tools.util.statemachie.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachie.State.GOT_THE_FIRST_TASK;

@Component
public class ChangeTaskActionConfig {

  private Action<State, Event> changeTaskAction;
  private Action<State, Event> errorAction;

  @Autowired
  public ChangeTaskActionConfig(Action<State, Event> changeTaskAction,
                                Action<State, Event> errorAction) {
    this.changeTaskAction = changeTaskAction;
    this.errorAction = errorAction;
  }

  public ExternalTransitionConfigurer<State, Event> configure(ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(CHECK_FOR_NEW_TASK)
        .target(GOT_THE_FIRST_TASK)
        .event(CHANGE_TASK)
        .action(changeTaskAction, errorAction);
  }
}
