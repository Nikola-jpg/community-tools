package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

public interface Transition extends Action<State, Event> {
  void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception;
}
