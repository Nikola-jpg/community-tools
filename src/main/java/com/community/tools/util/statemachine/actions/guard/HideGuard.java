package com.community.tools.util.statemachine.actions.guard;

import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;

import java.io.IOException;

import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.guard.Guard;

@WithStateMachine
public class HideGuard implements Guard<State, Event> {

  @Value("${checkNickName}")
  private String checkNickName;
  @Value("${failedNickName}")
  private String failedNickName;
  @Value("${addGitName}")
  private String addGitName;

  @Autowired
  private GitHubService gitHubService;

  @Autowired
  private Map<String, MessageService> messageServiceMap;

  @Value("${currentMessageService}")
  private String currentMessageService;

  /**
   * Selected current message service.
   * @return current message service
   */
  public MessageService getMessageService() {
    return messageServiceMap.get(currentMessageService);
  }

  @SneakyThrows
  @Override
  public boolean evaluate(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    String nickName = stateContext.getExtendedState().getVariables().get("gitNick").toString();
    getMessageService().sendPrivateMessage(getMessageService().getUserById(user),
          checkNickName + nickName);
    boolean nicknameMatch = false;
    try {
      nicknameMatch = gitHubService.getUserByLoginInGitHub(nickName).getLogin().equals(nickName);
    } catch (IOException e) {
      getMessageService().sendPrivateMessage(getMessageService().getUserById(user), failedNickName);
    }
    return nicknameMatch;
  }
}
