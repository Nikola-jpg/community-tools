package com.community.tools.util.statemachie.actions.transitions.verifications;

import static com.community.tools.util.statemachie.Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static com.community.tools.util.statemachie.State.CHECK_LOGIN;

import com.community.tools.service.MessageService;
import com.community.tools.service.payload.CheckLoginPayload;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class DidNotPassVerificationGitLoginTrans implements Transition {

  @Autowired
  private Action<State, Event> errorAction;
  @Value("${answeredNoDuringVerification}")
  private String answeredNoDuringVerification;
  @Autowired
  private MessageService messageService;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(CHECK_LOGIN)
        .target(AGREED_LICENSE)
        .event(DID_NOT_PASS_VERIFICATION_GIT_LOGIN)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    CheckLoginPayload payload = (CheckLoginPayload) stateContext.getExtendedState().getVariables()
        .get("dataPayload");
    String user = payload.getId();
    messageService.sendPrivateMessage(messageService.getUserById(user),
        answeredNoDuringVerification);
  }
}
