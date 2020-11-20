package com.community.tools.util.statemachie.actions;

import com.community.tools.model.User;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.io.IOException;

import lombok.SneakyThrows;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;


public class AddGitNameAction implements Action<State, Event> {

  @Value("${congratsAvailableNick}")
  private String congratsAvailableNick;
  @Value("${generalInformationChannel}")
  private String channel;
  @Autowired
  private SlackService slackService;
  @Autowired
  private StateMachineRepository stateMachineRepository;
  @Autowired
  private GitHubConnectService gitHubConnectService;
  @Autowired
  private GitHubService gitHubService;

  @SneakyThrows
  @Override
  public void execute(final StateContext<State, Event> context) {
    String user = context.getExtendedState().getVariables().get("id").toString();
    String nickname = context.getExtendedState().getVariables().get("gitNick").toString();

    User userEntity = stateMachineRepository.findByUserID(user).get();
    userEntity.setGitName(nickname);
    stateMachineRepository.save(userEntity);
    GHUser userGitLogin = new GHUser();
    try {
      userGitLogin = gitHubService.getUserByLoginInGitHub(nickname);
      gitHubConnectService.getGitHubRepository().getTeams()
          .stream().filter(e -> e.getName().equals("trainees")).findFirst()
          .get().add(userGitLogin);
    } catch (IOException e) {
      slackService.sendPrivateMessage(slackService.getUserById(user),
          "Something went wrong when adding to role. You need to contact the admin!");
    }
    slackService.sendPrivateMessage(slackService.getUserById(user), congratsAvailableNick);
    slackService.sendMessageToConversation(channel,
          generalInformationAboutUserToChannel(user, userGitLogin));
  }

  private String generalInformationAboutUserToChannel(String slackName, GHUser user) {
    return slackService.getUserById(slackName) + " - " + user.getLogin();
  }
}
