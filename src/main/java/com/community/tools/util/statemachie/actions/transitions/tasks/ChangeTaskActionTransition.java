package com.community.tools.util.statemachie.actions.transitions.tasks;

import static com.community.tools.util.statemachie.Event.CHANGE_TASK;
import static com.community.tools.util.statemachie.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachie.State.ESTIMATE_THE_TASK;
import static com.community.tools.util.statemachie.State.GOT_THE_NEXT_TASK;

import com.community.tools.service.MessageService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class ChangeTaskActionTransition implements Transition {

  @Autowired
  private Action<State, Event> errorAction;

  @Autowired
  private MessageService messageService;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(CHECK_FOR_NEW_TASK)
        .target(ESTIMATE_THE_TASK)//put ESTIMATING_TASK
        .event(CHANGE_TASK)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    int i = (Integer) stateContext.getExtendedState().getVariables().get("taskNumber");
    stateContext.getExtendedState().getVariables().put("taskNumber", ++i);
  }
}
