package com.community.tools.util.statemachie.actions.error;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.transitions.verifications.AgreeLicenseActionTransition;
import java.util.logging.Logger;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;

@WithStateMachine
public class ErrorAction implements Action<State, Event> {
  private static Logger logger = Logger.getLogger(AgreeLicenseActionTransition.class.getName());

  @Override
  public void execute(final StateContext<State, Event> context) {
    logger.info("Error with " + context.getTarget().getId());
  }
}
