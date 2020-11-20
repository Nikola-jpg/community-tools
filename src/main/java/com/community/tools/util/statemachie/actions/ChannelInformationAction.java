package com.community.tools.util.statemachie.actions;

import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.methods.SlackApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.io.IOException;

public class ChannelInformationAction implements Action<State, Event> {

  @Value("${messageAboutSeveralInfoChannel}")
  private String messageAboutSeveralInfoChannel;
  @Autowired
  private SlackService slackService;
  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    try {
      slackService.sendBlocksMessage(user, messageAboutSeveralInfoChannel);
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
