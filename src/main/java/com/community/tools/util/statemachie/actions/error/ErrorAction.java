package com.community.tools.util.statemachie.actions.error;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.verifications.AgreeLicenseAction;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.logging.Logger;

public class ErrorAction implements Action<State, Event> {
  private static Logger logger = Logger.getLogger(AgreeLicenseAction.class.getName());

  @Override
  public void execute(final StateContext<State, Event> context) {
    logger.info("Error with " + context.getTarget().getId());
  }
}
