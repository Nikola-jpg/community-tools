package com.community.tools.util.statemachie.actions.configs;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;

public interface ActionConfig {
  ExternalTransitionConfigurer<State, Event> configure(
      ExternalTransitionConfigurer<State, Event> transition) throws Exception;
}
