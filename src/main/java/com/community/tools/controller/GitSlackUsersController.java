package com.community.tools.controller;

import static com.community.tools.util.statemachie.Event.GET_THE_FIRST_TASK;
import static com.community.tools.util.statemachie.Event.QUESTION_FIRST;
import static com.community.tools.util.statemachie.State.GOT_THE_TASK;
import static com.community.tools.util.statemachie.State.NEW_USER;
import static org.springframework.http.ResponseEntity.ok;

import com.community.tools.service.MessageService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
  private final MessageService messageService;
  private final GitHubService gitService;

  @Value("${noOneCase}")
  private String noOneCase;
  @Value("${notThatMessage}")
  private String notThatMessage;

  /**
   * Endpoint /git. Method GET.
   *
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
   *
   * @return ResponseEntity with Status.OK and List of all users in Slack repository
   */
  @ApiOperation(value = "Returns list of slack users that work with the bot")
  @GetMapping(value = "/slack", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getSlackAllUsers() {
    Set<User> allSlackUsers = messageService.getAllUsers();

    List<String> listSlackUsersName = allSlackUsers.stream()
        .map(User::getProfile)
        .map(Profile::getDisplayName).collect(Collectors.toList());

    return ok().body(listSlackUsersName);
  }

  /**
   * Endpoint /sack/action. Method POST
   *
   * @param payload JSON of BlockActionPayload
   */
  @ApiOperation(value = "Deserializes Slack payload and sends message to user")
  @ApiImplicitParam(name = "payload", dataType = "string", paramType = "query",
      required = true, value = "payload")
  @RequestMapping(value = "/slack/action", method = RequestMethod.POST)
  public ResponseEntity<String> action(@RequestParam(name = "payload") String payload) {
    Gson snakeCase = GsonFactory.createSnakeCase();
    BlockActionPayload pl = snakeCase.fromJson(payload, BlockActionPayload.class);

    String userId = pl.getUser().getId();
    String action = pl.getActions().get(0).getActionId();

    String user = messageService.getUserById(userId);
    try {
      switch (action) {
        case "AGREE_LICENSE":
          if (!stateMachineService.doAction(userId, NEW_USER, QUESTION_FIRST)) {
            messageService.sendBlocksMessage(user, notThatMessage);
          }
          break;
        case "theEnd":
          if (stateMachineService.doAction(userId, GOT_THE_TASK, GET_THE_FIRST_TASK)) {
            messageService
                .sendPrivateMessage(user, "that was the end, congrats");
          } else {
            messageService.sendBlocksMessage(user, notThatMessage);
          }
          break;
        default:
          messageService.sendBlocksMessage(user, noOneCase);
      }
      return new ResponseEntity<>("Action: " + action,
          HttpStatus.OK);
    } catch (Exception ex) {
      return new ResponseEntity<>("Exception " + ex,
          HttpStatus.BAD_REQUEST);
    }
  }
}
