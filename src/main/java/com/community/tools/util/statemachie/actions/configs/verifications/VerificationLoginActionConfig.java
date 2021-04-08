package com.community.tools.util.statemachie.actions.configs.verifications;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

import static com.community.tools.util.statemachie.Event.LOGIN_CONFIRMATION;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static com.community.tools.util.statemachie.State.CHECK_LOGIN;

@Component
public class VerificationLoginActionConfig {

  private Action<State, Event> verificationLoginAction;
  private Action<State, Event> errorAction;

  @Autowired
  public VerificationLoginActionConfig(Action<State, Event> verificationLoginAction,
                                       Action<State, Event> errorAction) {
    this.verificationLoginAction = verificationLoginAction;
    this.errorAction = errorAction;
  }

  public ExternalTransitionConfigurer<State, Event> configure(ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(AGREED_LICENSE)
        .target(CHECK_LOGIN)
        .event(LOGIN_CONFIRMATION)
        .action(verificationLoginAction, errorAction);
  }
}
