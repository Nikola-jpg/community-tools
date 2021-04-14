package com.community.tools.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.community.tools.service.SendMessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.github.GitHubService;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.model.User.Profile;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;
import com.google.gson.Gson;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("app")
public class GitSlackUsersController {

  private final StateMachineService stateMachineService;
  private final SendMessageService sendMessageService;
  private final GitHubService gitService;

  /**
   * Endpoint /git. Method GET.
   * @return ResponseEntity with Status.OK and List of all users in GH repository
   */
  @ApiOperation(value = "Returns list of github logins"
          + " of Broscorp-net/traineeship collaborators")
  @GetMapping(value = "/git", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getGitHubAllUsers() {
    Set<GHUser> gitHubAllUsers = gitService.getGitHubAllUsers();

    List<String> listGitUsersLogin = gitHubAllUsers.stream().map(GHPerson::getLogin)
        .collect(Collectors.toList());

    return ok().body(listGitUsersLogin);
  }

  /**
   * Endpoint /slack. Method GET.
   * @return ResponseEntity with Status.OK and List of all users in Slack repository
   */
  @ApiOperation(value = "Returns list of slack users that work with the bot")
  @GetMapping(value = "/slack", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getSlackAllUsers() {
    Set<User> allSlackUsers = sendMessageService.getAllUsers();

    List<String> listSlackUsersName = allSlackUsers.stream()
        .map(User::getProfile)
        .map(Profile::getDisplayName).collect(Collectors.toList());

    return ok().body(listSlackUsersName);
  }

  /**
   * Endpoint /sack/action. Method POST
   * @param payload JSON of BlockActionPayload
   * @throws Exception Exception
   */
  @ApiOperation(value = "Deserializes Slack payload and sends message to user")
  @ApiImplicitParam(name = "payload", dataType = "string", paramType = "query",
      required = true, value = "payload")
  @RequestMapping(value = "/slack/action", method = RequestMethod.POST)
  public void action(@RequestParam(name = "payload") String payload) throws Exception {

    Gson snakeCase = GsonFactory.createSnakeCase();
    BlockActionPayload pl = snakeCase.fromJson(payload, BlockActionPayload.class);

    stateMachineService.checkActionsFromButton(pl.getActions()
            .get(0).getValue(),pl.getUser().getId());
  }
}
