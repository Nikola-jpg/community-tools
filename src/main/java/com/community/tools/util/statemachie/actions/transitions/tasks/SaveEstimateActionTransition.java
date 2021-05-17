package com.community.tools.util.statemachie.actions.transitions.tasks;

import static com.community.tools.util.statemachie.Event.SAVE_ESTIMATE;
import static com.community.tools.util.statemachie.State.CHECK_ESTIMATE;
import static com.community.tools.util.statemachie.State.GOT_THE_TASK;

import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.payload.EstimatePayload;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.Transition;
import com.community.tools.util.statemachie.actions.error.ErrorAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class SaveEstimateActionTransition implements Transition {

  @Autowired
  ErrorAction errorAction;

  @Autowired
  MessageService messageService;

  @Autowired
  StateMachineService stateMachineService;

  @Override
  public void configure(StateMachineTransitionConfigurer<State, Event> transitions)
      throws Exception {
    transitions
        .withExternal()
        .source(CHECK_ESTIMATE)
        .target(GOT_THE_TASK)
        .event(SAVE_ESTIMATE)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    EstimatePayload payload = (EstimatePayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String user = payload.getId();
    String value = payload.getValue().toString();

    try {
      stateMachineService.estimate(value, user);
    } catch (Exception e) {
      messageService
          .sendPrivateMessage(messageService.getUserById(user), "Error with saved estimate!");
    }
  }
}
