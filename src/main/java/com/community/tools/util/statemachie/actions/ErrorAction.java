package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;


public class ErrorAction implements Action<State, Event> {

  @Override
  public void execute(final StateContext<State, Event> context) {
    System.out.println("Ошибка при переходе в статус " + context.getTarget().getId());
  }
}
