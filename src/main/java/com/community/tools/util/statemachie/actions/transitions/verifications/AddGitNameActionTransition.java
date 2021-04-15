package com.community.tools.util.statemachie.actions.transitions.verifications;

import static com.community.tools.util.statemachie.Event.ADD_GIT_NAME;
import static com.community.tools.util.statemachie.State.ADDED_GIT;
import static com.community.tools.util.statemachie.State.CHECK_LOGIN;

import com.community.tools.model.User;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.Transition;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.io.IOException;
import lombok.SneakyThrows;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class AddGitNameActionTransition implements Transition {

  @Autowired
  private Action<State, Event> errorAction;
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

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(CHECK_LOGIN)
        .target(ADDED_GIT)
        .event(ADD_GIT_NAME)
        .action(this, errorAction);
  }

  @SneakyThrows
  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    String nickname = stateContext.getExtendedState().getVariables().get("gitNick").toString();

    User stateEntity = stateMachineRepository.findByUserID(user).get();
    stateEntity.setGitName(nickname);
    stateMachineRepository.save(stateEntity);
    String firstAnswer = stateEntity.getFirstAnswerAboutRules();
    String secondAnswer = stateEntity.getSecondAnswerAboutRules();
    String thirdAnswer = stateEntity.getThirdAnswerAboutRules();
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
        generalInformationAboutUserToChannel(user, userGitLogin)
            + "\n" + sendUserAnswersToChannel(firstAnswer, secondAnswer, thirdAnswer));
  }

  private String generalInformationAboutUserToChannel(String slackName, GHUser user) {
    return slackService.getUserById(slackName) + " - " + user.getLogin();
  }

  private String sendUserAnswersToChannel(String firstAnswer, String secondAnswer,
                                          String thirdAnswer) {
    return "Answer on questions : \n"
        + "1. " + firstAnswer + ";\n"
        + "2. " + secondAnswer + ";\n"
        + "3. " + thirdAnswer + ".";
  }
}
