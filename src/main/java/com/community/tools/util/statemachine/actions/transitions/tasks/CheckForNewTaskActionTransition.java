package com.community.tools.util.statemachine.actions.transitions.tasks;

import static com.community.tools.util.statemachine.Event.GET_THE_NEW_TASK;
import static com.community.tools.util.statemachine.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachine.State.GOT_THE_TASK;

import com.community.tools.model.Messages;
import com.community.tools.service.BlockService;
import com.community.tools.service.MessageService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class CheckForNewTaskActionTransition implements Transition {

  @Autowired
  private MessageService messageService;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @Autowired
  private Action<State, Event> errorAction;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(GOT_THE_TASK)
        .target(CHECK_FOR_NEW_TASK)
        .event(GET_THE_NEW_TASK)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    List<String> tasksList = Arrays.asList(tasksForUsers);

    int i = (Integer) stateContext.getExtendedState().getVariables().get("taskNumber");
    String taskMessage =
        "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"Here is your next <https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/"
            + tasksList.get(i) + "|TASK>.\"}}]";
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    messageService.sendBlocksMessage(messageService.getUserById(user),
        messageService.createBlockMessage(taskMessage,
            new EmbedBuilder()
                .addField("", Messages.NEXT_TASK + tasksList.get(i) + ") :link:", false)
                .build()));
  }
}
