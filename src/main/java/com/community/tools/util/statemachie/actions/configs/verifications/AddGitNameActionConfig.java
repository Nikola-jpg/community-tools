package com.community.tools.util.statemachie.actions.configs.verifications;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

import static com.community.tools.util.statemachie.Event.ADD_GIT_NAME;
import static com.community.tools.util.statemachie.State.ADDED_GIT;
import static com.community.tools.util.statemachie.State.CHECK_LOGIN;

@Component
public class AddGitNameActionConfig {

  private Action<State, Event> addGitNameAction;
  private Action<State, Event> errorAction;

  @Autowired
  public AddGitNameActionConfig(Action<State, Event> addGitNameAction,
                                Action<State, Event> errorAction) {
    this.addGitNameAction = addGitNameAction;
    this.errorAction = errorAction;
  }

  public ExternalTransitionConfigurer<State, Event> configure(ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(CHECK_LOGIN)
        .target(ADDED_GIT)
        .event(ADD_GIT_NAME)
        .action(addGitNameAction, errorAction);
  }
}
