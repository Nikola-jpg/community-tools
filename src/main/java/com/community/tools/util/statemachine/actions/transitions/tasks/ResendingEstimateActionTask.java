package com.community.tools.util.statemachine.actions.transitions.tasks;

import static com.community.tools.util.statemachine.Event.RESENDING_ESTIMATE_TASK;
import static com.community.tools.util.statemachine.State.ESTIMATE_THE_TASK;
import static com.community.tools.util.statemachine.State.GOT_THE_TASK;

import com.community.tools.service.MessageService;
import com.community.tools.service.MessagesToPlatform;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.payload.SimplePayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import com.community.tools.util.statemachine.actions.error.ErrorAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class ResendingEstimateActionTask implements Transition {

  @Autowired
  ErrorAction errorAction;

  @Autowired
  MessageService messageService;

  @Autowired
  StateMachineService stateMachineService;

  @Autowired
  MessagesToPlatform messagesToPlatform;


  @Override
  public void configure(StateMachineTransitionConfigurer<State, Event> transitions)
      throws Exception {
    transitions
        .withExternal()
        .source(GOT_THE_TASK)
        .target(ESTIMATE_THE_TASK)
        .event(RESENDING_ESTIMATE_TASK)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    SimplePayload payload = (SimplePayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String user = payload.getId();

    messageService
        .sendBlocksMessage(messageService.getUserById(user), messagesToPlatform.estimateTheTask);
  }
}
