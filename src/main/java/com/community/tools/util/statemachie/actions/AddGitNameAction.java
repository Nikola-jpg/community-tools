package com.community.tools.util.statemachie.actions;

import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;


public class AddGitNameAction implements Action<State, Event> {

  @Value("${congratsAvailableNick}")
  private String congratsAvailableNick;
  @Autowired
  private SlackService slackService;
  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Override
  public void execute(final StateContext<State, Event> context) {
    String user = context.getExtendedState().getVariables().get("id").toString();
    String nickname = context.getExtendedState().getVariables().get("gitNick").toString();
    StateEntity stateEntity = stateMachineRepository.findByUserID(user).get();
    stateEntity.setGitName(nickname);
    stateMachineRepository.save(stateEntity);
    try {
      slackService.sendPrivateMessage(slackService.getUserById(user), congratsAvailableNick);
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
