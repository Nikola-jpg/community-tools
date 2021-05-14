package com.community.tools.util.statemachie.actions.transitions.verifications;

import static com.community.tools.util.statemachie.Event.ADD_GIT_NAME_AND_FIRST_TASK;
import static com.community.tools.util.statemachie.State.CHECK_LOGIN;
import static com.community.tools.util.statemachie.State.ESTIMATE_THE_TASK;

import com.community.tools.model.User;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.payload.VerificationPayload;
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
  @Value("${generalInformationChannel}")
  private String channel;
  @Value("${getFirstTask}")
  private String getFirstTask;
  @Value("${errorWithAddingGitName}")
  private String errorWithAddingGitName;
  @Value("${testModeSwitcher}")
  private Boolean testModeSwitcher;
  @Autowired
  private MessageService messageService;
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
        .target(ESTIMATE_THE_TASK)
        .event(ADD_GIT_NAME_AND_FIRST_TASK)
        .action(this, errorAction);
  }

  @SneakyThrows
  @Override
  public void execute(StateContext<State, Event> stateContext) {
    VerificationPayload payload = (VerificationPayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String user = payload.getId();
    String nickname = payload.getGitNick();

    User stateEntity = stateMachineRepository.findByUserID(user).get();
    stateEntity.setGitName(nickname);
    stateMachineRepository.save(stateEntity);
    String firstAnswer = stateEntity.getFirstAnswerAboutRules();
    String secondAnswer = stateEntity.getSecondAnswerAboutRules();
    String thirdAnswer = stateEntity.getThirdAnswerAboutRules();
    if (!testModeSwitcher) {
      GHUser userGitLogin = new GHUser();
      try {
        userGitLogin = gitHubService.getUserByLoginInGitHub(nickname);
        gitHubConnectService.getGitHubRepository().getTeams()
            .stream().filter(e -> e.getName().equals("trainees")).findFirst()
            .get().add(userGitLogin);
      } catch (IOException e) {
        messageService.sendBlocksMessage(messageService.getUserById(user),
            errorWithAddingGitName);
      }
      messageService.sendMessageToConversation(channel,
          generalInformationAboutUserToChannel(user, userGitLogin)
              + "\n" + sendUserAnswersToChannel(firstAnswer, secondAnswer, thirdAnswer));
    }
    messageService.sendBlocksMessage(messageService.getUserById(user), getFirstTask);
    stateContext.getExtendedState().getVariables().put("gitNick", nickname);
  }

  private String generalInformationAboutUserToChannel(String slackName, GHUser user) {
    return messageService.getUserById(slackName) + " - " + user.getLogin();
  }

  private String sendUserAnswersToChannel(String firstAnswer, String secondAnswer,
      String thirdAnswer) {
    return "Answer on questions : \n"
        + "1. " + firstAnswer + ";\n"
        + "2. " + secondAnswer + ";\n"
        + "3. " + thirdAnswer + ".";
  }
}
