package com.community.tools.util.statemachie.actions;

import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;


public class AddGitNameAction implements Action<State, Event> {

  @Value("${congratsAvailableNick}")
  private String congratsAvailableNick;
  @Autowired
  private SlackService slackService;
  @Autowired
  private StateMachineRepository stateMachineRepository;
  @Autowired
  private GitHubConnectService gitHubConnectService;
  @Autowired
  private GitHubService gitHubService;

  @Override
  public void execute(final StateContext<State, Event> context) {
    String user = context.getExtendedState().getVariables().get("id").toString();
    String nickname = context.getExtendedState().getVariables().get("gitNick").toString();
    GHUser userGitLogin;
    StateEntity stateEntity = stateMachineRepository.findByUserID(user).get();
    stateEntity.setGitName(nickname);
    stateMachineRepository.save(stateEntity);
    try {
      userGitLogin = gitHubService.getUserByLoginInGitHub(nickname);
      gitHubConnectService.getGitHubRepository().getTeams()
          .stream().filter(e -> e.getName().equals("trainees")).findFirst()
          .get().add(userGitLogin);
      slackService.sendPrivateMessage(slackService.getUserById(user), congratsAvailableNick);
      slackService.sendMessageToConversation("test3",
          generalInformationAboutUserToChannel(user, userGitLogin));
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  private String generalInformationAboutUserToChannel(String slackName, GHUser user) {
    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat("E yyyy.MM.dd 'в' hh:mm:ss a zzz");
    return slackService.getUserById(slackName)
        + " прочитал правила и ввел ник - " + user.getLogin() + " в " + format.format(date);
  }
}
