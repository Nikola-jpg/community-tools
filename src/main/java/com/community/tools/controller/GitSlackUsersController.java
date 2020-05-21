package com.community.tools.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.model.User.Profile;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;
import com.google.gson.Gson;
import java.io.IOException;
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
  public void action(
      @RequestParam(name = "payload") String payload
  ) {
    Gson snakeCase = GsonFactory.createSnakeCase();
    BlockActionPayload pl = snakeCase.fromJson(payload, BlockActionPayload.class);

    try {
      usersService.sendPrivateMessage("roman",
          "Message : \n\n  Name:\n "+pl.getUser().getName()+"\n\n"
              +"User:\n "+pl.getUser()+"\n\n"
              +" Type:\n "+pl.getType()+"\n\n"
              +" AppID:\n " +pl.getApiAppId() +"\n\n"
              +" ResponseUrl:\n " +pl.getResponseUrl() +"\n\n"
              +" Token:\n " +pl.getToken() +"\n\n"
              +" TriggerId:\n " +pl.getTriggerId() +"\n\n"
              +" Actions:\n " +pl.getActions() +"\n\n"
              +" Channel:\n " +pl.getChannel() +"\n\n"
              +" Container: \n" +pl.getContainer() +"\n\n"
              +" Team:\n " +pl.getTeam() +"\n\n"
              +" Message:\n " +pl.getMessage() +"\n\n"
              );
    } catch (IOException | SlackApiException e) {
      throw  new RuntimeException(e);
    }
  }
}
