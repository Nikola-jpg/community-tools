package com.community.tools.util.statemachie.actions.configs.tasks;

import static com.community.tools.util.statemachie.Event.GET_THE_FIRST_TASK;
import static com.community.tools.util.statemachie.State.ADDED_GIT;
import static com.community.tools.util.statemachie.State.GOT_THE_FIRST_TASK;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.configs.ActionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

@Component
public class GetTheFirstTaskActionConfig implements ActionConfig {

  private Action<State, Event> getTheFirstTaskAction;
  private Action<State, Event> errorAction;

  @Autowired
  public GetTheFirstTaskActionConfig(Action<State, Event> getTheFirstTaskAction,
                                     Action<State, Event> errorAction) {
    this.getTheFirstTaskAction = getTheFirstTaskAction;
    this.errorAction = errorAction;
  }

  @Override
  public ExternalTransitionConfigurer<State, Event> configure(
      ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(ADDED_GIT)
        .target(GOT_THE_FIRST_TASK)
        .event(GET_THE_FIRST_TASK)
        .action(getTheFirstTaskAction, errorAction);
  }
}
