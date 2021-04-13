package com.community.tools.util.statemachie.actions.configs.verifications;

import static com.community.tools.util.statemachie.Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static com.community.tools.util.statemachie.State.CHECK_LOGIN;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.configs.ActionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

@Component
public class DidNotPassVerificationGitLoginConf implements ActionConfig {

  private Action<State, Event> didntPassVerificationGitLogin;
  private Action<State, Event> errorAction;

  @Autowired
  public DidNotPassVerificationGitLoginConf(Action<State, Event> didntPassVerificationGitLogin,
                                            Action<State, Event> errorAction) {
    this.didntPassVerificationGitLogin = didntPassVerificationGitLogin;
    this.errorAction = errorAction;
  }

  @Override
  public ExternalTransitionConfigurer<State, Event> configure(
      ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(CHECK_LOGIN)
        .target(AGREED_LICENSE)
        .event(DID_NOT_PASS_VERIFICATION_GIT_LOGIN)
        .action(didntPassVerificationGitLogin, errorAction);
  }
}
