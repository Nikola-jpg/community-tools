package com.community.tools.util.statemachie.actions.transitions.tasks;

import static com.community.tools.util.statemachie.Event.LAST_TASK;
import static com.community.tools.util.statemachie.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachie.State.ESTIMATE_THE_TASK;

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
import org.springframework.statemachine.guard.Guard;

@WithStateMachine
public class LastTaskActionTransition implements Transition {

  @Autowired
  private Action<State, Event> errorAction;

  @Autowired
  private Guard<State, Event> lastTaskGuard;

  @Value("${lastTask}")
  private String lastTask;

  @Autowired
  private MessageService messageService;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(CHECK_FOR_NEW_TASK)
        .target(ESTIMATE_THE_TASK)
        .event(LAST_TASK)
        .guard(lastTaskGuard)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    messageService.sendPrivateMessage(messageService.getUserById(user), lastTask);
  }
}
