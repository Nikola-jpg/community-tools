package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.PurchaseEvent;
import com.community.tools.util.statemachie.PurchaseState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;


public class HideGuard implements Guard<PurchaseState, PurchaseEvent> {

  @Override
  public boolean evaluate(StateContext<PurchaseState, PurchaseEvent> stateContext) {
    return true;
  }
}
