package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.PurchaseEvent;
import com.community.tools.util.statemachie.PurchaseState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;


public class ErrorAction implements Action<PurchaseState, PurchaseEvent> {

  @Override
  public void execute(final StateContext<PurchaseState, PurchaseEvent> context) {
    System.out.println("Ошибка при переходе в статус " + context.getTarget().getId());
  }
}
