package com.community.tools.util.statemachie.actions.configs.verifications;

import static com.community.tools.util.statemachie.Event.ADD_GIT_NAME;
import static com.community.tools.util.statemachie.State.ADDED_GIT;
import static com.community.tools.util.statemachie.State.CHECK_LOGIN;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.configs.ActionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;

public class AddGitNameActionConfig implements ActionConfig {

  private Action<State, Event> addGitNameAction;
  private Action<State, Event> errorAction;

  @Autowired
  public AddGitNameActionConfig(Action<State, Event> addGitNameAction,
                                Action<State, Event> errorAction) {
    this.addGitNameAction = addGitNameAction;
    this.errorAction = errorAction;
  }

  @Override
  public ExternalTransitionConfigurer<State, Event> configure(
      ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(CHECK_LOGIN)
        .target(ADDED_GIT)
        .event(ADD_GIT_NAME)
        .action(addGitNameAction, errorAction);
  }
}
