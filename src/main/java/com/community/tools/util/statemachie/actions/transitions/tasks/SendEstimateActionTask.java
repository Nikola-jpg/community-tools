package com.community.tools.util.statemachie.actions.transitions.tasks;

import static com.community.tools.util.statemachie.Event.SEND_ESTIMATE_TASK;
import static com.community.tools.util.statemachie.State.ESTIMATE_THE_TASK;
import static com.community.tools.util.statemachie.State.GOT_THE_TASK;

import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.payload.SinglePayload;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.Transition;
import com.community.tools.util.statemachie.actions.error.ErrorAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class SendEstimateActionTask implements Transition {

  @Autowired
  ErrorAction errorAction;

  @Autowired
  MessageService messageService;

  @Autowired
  StateMachineService stateMachineService;

  @Value("${estimateTheTask}")
  String estimateTheTask;

  @Override
  public void configure(StateMachineTransitionConfigurer<State, Event> transitions)
      throws Exception {
    transitions
        .withExternal()
        .source(GOT_THE_TASK)
        .target(ESTIMATE_THE_TASK)
        .event(SEND_ESTIMATE_TASK)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    SinglePayload payload = (SinglePayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String user = payload.getId();

    messageService.sendBlocksMessage(messageService.getUserById(user), estimateTheTask);
  }
}
