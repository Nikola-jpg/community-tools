package com.community.tools.util.statemachie.actions.tasks;

import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;


public class GetTheFirstTaskAction implements Action<State, Event> {

  @Value("${getFirstTask}")
  private String getFirstTask;
  @Autowired
  private SlackService slackService;

  @Override
  public void execute(final StateContext<State, Event> context) {
    String user = context.getExtendedState().getVariables().get("id").toString();
    slackService.sendBlocksMessage(slackService.getUserById(user), getFirstTask);
  }
}
