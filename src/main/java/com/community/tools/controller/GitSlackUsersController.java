package com.community.tools.controller;

import static com.community.tools.util.statemachie.Event.AGREE_LICENSE;
import static com.community.tools.util.statemachie.State.GOT_THE_FIRST_TASK;
import static com.community.tools.util.statemachie.State.NEW_USER;
import static org.springframework.http.ResponseEntity.ok;

import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.model.User.Profile;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;
import com.google.gson.Gson;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("app")
public class GitSlackUsersController {

  @Autowired
  private StateMachineFactory<State, Event> factory;
  @Autowired
  private StateMachinePersister<State, Event, String> persister;
  @Value("${addGitName}")
  private String addGitName;
  @Value("${noOneCase}")
  private String noOneCase;
  @Value("${notThatMessage}")
  private String notThatMessage;

  private final SlackService usersService;
  private final GitHubService gitService;

  @GetMapping(value = "/git", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getGitHubAllUsers() {
    Set<GHUser> gitHubAllUsers = gitService.getGitHubAllUsers();

    List<String> listGitUsersLogin = gitHubAllUsers.stream().map(GHPerson::getLogin)
        .collect(Collectors.toList());

    return ok().body(listGitUsersLogin);
  }

  @GetMapping(value = "/slack", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getSlackAllUsers() {
    Set<User> allSlackUsers = usersService.getAllUsers();

    List<String> listSlackUsersName = allSlackUsers.stream()
        .map(User::getProfile)
        .map(Profile::getDisplayName).collect(Collectors.toList());

    return ok().body(listSlackUsersName);
  }


  @RequestMapping(value = "/slack/action", method = RequestMethod.POST)
  public void action(@RequestParam(name = "payload") String payload) throws Exception {

    Gson snakeCase = GsonFactory.createSnakeCase();
    BlockActionPayload pl = snakeCase.fromJson(payload, BlockActionPayload.class);

    StateMachine<State, Event> machine = factory.getStateMachine();
    String user = usersService.getUserById(pl.getUser().getId());

    switch (pl.getActions().get(0).getValue()) {
      case "AGREE_LICENSE":
        persister.restore(machine, pl.getUser().getId());
        if (machine.getState().getId() == NEW_USER) {
          machine.sendEvent(AGREE_LICENSE);
          persister.persist(machine, pl.getUser().getId());
          usersService.sendBlocksMessage(user, addGitName);
        } else {
          usersService.sendBlocksMessage(user, notThatMessage);
        }
        break;
      case "theEnd":
        persister.restore(machine, pl.getUser().getId());
        if (machine.getState().getId() == GOT_THE_FIRST_TASK) {
          usersService
              .sendPrivateMessage(user, "that was the end, congrats");
        } else {
          usersService.sendBlocksMessage(user, notThatMessage);
        }
        break;
      default:
        usersService.sendBlocksMessage(user, noOneCase);

    }
  }
}
