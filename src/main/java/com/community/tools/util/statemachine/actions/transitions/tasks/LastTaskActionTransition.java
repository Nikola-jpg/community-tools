package com.community.tools.util.statemachine.actions.transitions.tasks;

import static com.community.tools.util.statemachine.Event.LAST_TASK;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import java.util.Map;
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
  private Map<String, MessageService> messageServiceMap;

  @Value("${currentMessageService}")
  private String currentMessageService;

  /**
   * Selected current message service.
   * @return current message service
   */
  public MessageService getMessageService() {
    return messageServiceMap.get(currentMessageService);
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.CHECK_FOR_NEW_TASK)
        .target(State.CONGRATS_LAST_TASK)
        .event(LAST_TASK)
        .guard(lastTaskGuard)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    getMessageService().sendPrivateMessage(getMessageService().getUserById(user),
        Messages.LAST_TASK);
  }
}
