package com.community.tools.util.statemachie.actions.configs.tasks;

import static com.community.tools.util.statemachie.Event.GET_THE_NEW_TASK;
import static com.community.tools.util.statemachie.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachie.State.GOT_THE_FIRST_TASK;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.configs.ActionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

@Component
public class CheckForNewTaskActionConfig implements ActionConfig {

  private Action<State, Event> checkForNewTaskAction;
  private Action<State, Event> errorAction;

  @Autowired
  public CheckForNewTaskActionConfig(Action<State, Event> checkForNewTaskAction,
                                     Action<State, Event> errorAction) {
    this.checkForNewTaskAction = checkForNewTaskAction;
    this.errorAction = errorAction;
  }

  @Override
  public ExternalTransitionConfigurer<State, Event> configure(
      ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(GOT_THE_FIRST_TASK)
        .target(CHECK_FOR_NEW_TASK)
        .event(GET_THE_NEW_TASK)
        .action(checkForNewTaskAction, errorAction);
  }
}
