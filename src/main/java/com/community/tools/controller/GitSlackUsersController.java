package com.community.tools.controller;

import com.community.tools.service.StateMachineService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.model.User.Profile;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;
import com.google.gson.Gson;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("app")
public class GitSlackUsersController {

  private final StateMachineService stateMachineService;
  private final SlackService slackService;
  private final GitHubService gitService;

  @ApiOperation(value = "Returns list of github logins"
          + " of Broscorp-net/traineeship collaborators")
  @GetMapping(value = "/git", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getGitHubAllUsers() {
    Set<GHUser> gitHubAllUsers = gitService.getGitHubAllUsers();

    List<String> listGitUsersLogin = gitHubAllUsers.stream().map(GHPerson::getLogin)
        .collect(Collectors.toList());

    return ok().body(listGitUsersLogin);
  }

  @ApiOperation(value = "Returns list of slack users that work with the bot")
  @GetMapping(value = "/slack", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getSlackAllUsers() {
    Set<User> allSlackUsers = slackService.getAllUsers();

    List<String> listSlackUsersName = allSlackUsers.stream()
        .map(User::getProfile)
        .map(Profile::getDisplayName).collect(Collectors.toList());

    return ok().body(listSlackUsersName);
  }

  @ApiOperation(value = "Deserializes Slack payload and sends message to user")
  @ApiImplicitParam(name = "payload", dataType = "string", paramType = "query",
    required = true, value = "payload")
  @RequestMapping(value = "/slack/action", method = RequestMethod.POST)
  public void action(@RequestParam(name = "payload") String payload) throws Exception {

    Gson snakeCase = GsonFactory.createSnakeCase();
    BlockActionPayload pl = snakeCase.fromJson(payload, BlockActionPayload.class);

    stateMachineService.checkActionsFromButton(pl.getActions().get(0).getValue(),pl.getUser().getId());
  }
}
