package com.community.tools.util.statemachine.actions.transitions.verifications;

import static com.community.tools.util.statemachine.Event.LOGIN_CONFIRMATION;
import static com.community.tools.util.statemachine.State.AGREED_LICENSE;
import static com.community.tools.util.statemachine.State.CHECK_LOGIN;

import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import java.io.IOException;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class VerificationLoginActionTransition implements Transition {

  @Autowired
  private Action<State, Event> errorAction;
  @Value("${askAboutProfile}")
  private String askAboutProfile;
  @Autowired
  private MessageService messageService;
  @Autowired
  private GitHubService gitHubService;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(AGREED_LICENSE)
        .target(CHECK_LOGIN)
        .event(LOGIN_CONFIRMATION)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    VerificationPayload payload = (VerificationPayload) stateContext.getExtendedState()
        .getVariables()
        .get("dataPayload");
    String id = payload.getId();
    String nickname = payload.getGitNick();
    try {
      GHUser userGitLogin = gitHubService.getUserByLoginInGitHub(nickname);
      messageService.sendPrivateMessage(messageService.getUserById(id),
          askAboutProfile + "\n" + userGitLogin.getHtmlUrl().toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
