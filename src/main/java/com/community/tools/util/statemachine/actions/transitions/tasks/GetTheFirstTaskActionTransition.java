package com.community.tools.util.statemachine.actions.transitions.tasks;

import static com.community.tools.util.statemachine.Event.GET_THE_FIRST_TASK;
import static com.community.tools.util.statemachine.State.ADDED_GIT;
import static com.community.tools.util.statemachine.State.GOT_THE_TASK;

import com.community.tools.service.BlockService;
import com.community.tools.service.MessageService;
import com.community.tools.service.MessagesToPlatform;
import com.community.tools.service.discord.MessagesToDiscord;
import com.community.tools.service.payload.SinglePayload;
import com.community.tools.service.slack.MessagesToSlack;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class GetTheFirstTaskActionTransition implements Transition {

  @Value("${getFirstTask}")
  private String getFirstTask;

  @Autowired
  private MessageService messageService;

  @Autowired
  private Action<State, Event> errorAction;

  @Autowired
  private MessagesToPlatform messagesToPlatform;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(ADDED_GIT)
        .target(GOT_THE_TASK)
        .event(GET_THE_FIRST_TASK)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    SinglePayload payload = (SinglePayload) stateContext.getExtendedState().getVariables()
        .get("dataPayload");
    String user = payload.getId();
    messageService.sendBlocksMessage(messageService.getUserById(user),
        messageService.createBlockMessage(messagesToPlatform.GET_FIRST_TASK));
  }
}
