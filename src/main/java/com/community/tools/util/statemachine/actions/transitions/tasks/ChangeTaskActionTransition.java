package com.community.tools.util.statemachine.actions.transitions.tasks;

import static com.community.tools.util.statemachine.Event.CHANGE_TASK;
import static com.community.tools.util.statemachine.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachine.State.ESTIMATE_THE_TASK;

import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class ChangeTaskActionTransition implements Transition {

  @Autowired
  private Action<State, Event> errorAction;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(CHECK_FOR_NEW_TASK)
        .target(ESTIMATE_THE_TASK)
        .event(CHANGE_TASK)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    int i = (Integer) stateContext.getExtendedState().getVariables().get("taskNumber");
    stateContext.getExtendedState().getVariables().put("taskNumber", ++i);
  }
}
