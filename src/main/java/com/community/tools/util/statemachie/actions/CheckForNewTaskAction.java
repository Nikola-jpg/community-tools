package com.community.tools.util.statemachie.actions;

import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class CheckForNewTaskAction implements Action<State, Event> {
  @Autowired
  private SlackService slackService;
  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @Override
  public void execute(final StateContext<State, Event> context) {
    List<String> tasksList = Arrays.asList(tasksForUsers);

    int i = (Integer)context.getExtendedState().getVariables().get("taskNumber");
    String taskMessage =
          "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"Here is your next <https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/"
              + tasksList.get(i) + "|TASK>.\"}}]";
    String user = context.getExtendedState().getVariables().get("id").toString();
    slackService.sendBlocksMessage(slackService.getUserById(user), taskMessage);
  }
}