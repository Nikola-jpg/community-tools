package com.community.tools.util.statemachie.actions.configs.tasks;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

import static com.community.tools.util.statemachie.Event.GET_THE_NEW_TASK;
import static com.community.tools.util.statemachie.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachie.State.GOT_THE_FIRST_TASK;

@Component
public class CheckForNewTaskActionConfig {

  private Action<State, Event> checkForNewTaskAction;
  private Action<State, Event> errorAction;

  @Autowired
  public CheckForNewTaskActionConfig(Action<State, Event> checkForNewTaskAction,
                                     Action<State, Event> errorAction) {
    this.checkForNewTaskAction = checkForNewTaskAction;
    this.errorAction = errorAction;
  }

  public ExternalTransitionConfigurer<State, Event> configure(ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(GOT_THE_FIRST_TASK)
        .target(CHECK_FOR_NEW_TASK)
        .event(GET_THE_NEW_TASK)
        .action(checkForNewTaskAction, errorAction);
  }
}
