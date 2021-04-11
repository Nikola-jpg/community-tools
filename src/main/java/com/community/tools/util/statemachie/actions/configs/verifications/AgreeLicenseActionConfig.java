package com.community.tools.util.statemachie.actions.configs.verifications;

import static com.community.tools.util.statemachie.Event.AGREE_LICENSE;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static com.community.tools.util.statemachie.State.INFORMATION_CHANNELS;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.configs.ActionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;

public class AgreeLicenseActionConfig implements ActionConfig {

  private Action<State, Event> agreeLicenseAction;
  private Action<State, Event> errorAction;

  @Autowired
  public AgreeLicenseActionConfig(Action<State, Event> agreeLicenseAction,
                                  Action<State, Event> errorAction) {
    this.agreeLicenseAction = agreeLicenseAction;
    this.errorAction = errorAction;
  }

  @Override
  public ExternalTransitionConfigurer<State, Event> configure(
      ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(INFORMATION_CHANNELS)
        .target(AGREED_LICENSE)
        .event(AGREE_LICENSE)
        .action(agreeLicenseAction, errorAction);
  }
}
