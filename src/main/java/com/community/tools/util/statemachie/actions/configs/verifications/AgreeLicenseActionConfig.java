package com.community.tools.util.statemachie.actions.configs.verifications;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

import static com.community.tools.util.statemachie.Event.AGREE_LICENSE;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static com.community.tools.util.statemachie.State.INFORMATION_CHANNELS;

@Component
public class AgreeLicenseActionConfig {

  private Action<State, Event> agreeLicenseAction;
  private Action<State, Event> errorAction;

  @Autowired
  public AgreeLicenseActionConfig(Action<State, Event> agreeLicenseAction,
                                  Action<State, Event> errorAction) {
    this.agreeLicenseAction = agreeLicenseAction;
    this.errorAction = errorAction;
  }

  public ExternalTransitionConfigurer<State, Event> configure(ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(INFORMATION_CHANNELS)
        .target(AGREED_LICENSE)
        .event(AGREE_LICENSE)
        .action(agreeLicenseAction, errorAction);
  }
}
