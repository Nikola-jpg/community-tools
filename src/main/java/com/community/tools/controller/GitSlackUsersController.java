package com.community.tools.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.community.tools.service.github.GitHubUsersService;
import com.community.tools.service.slack.SlackUsersService;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.model.User.Profile;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class GitSlackUsersController {

  private final SlackUsersService usersService;
  private final GitHubUsersService gitUsersService;

  @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getAllUsers(@RequestParam(name = "app") String app) {
    if (app.equals("git")) {
      Set<GHUser> gitHubAllUsers = gitUsersService.getGitHubAllUsers();

      List<String> listGitUsersLogin = gitHubAllUsers.stream().map(GHPerson::getLogin)
          .collect(Collectors.toList());

      return ok().body(listGitUsersLogin);
    }

    if (app.equals("slack")) {
      Set<User> allSlackUsers = usersService.getAllUsers();

      List<String> listSlackUsersName = allSlackUsers.stream()
          .map(User::getProfile)
          .map(Profile::getDisplayName).collect(Collectors.toList());

      return ok().body(listSlackUsersName);
    }
    throw new RuntimeException("you enter wrong app");
  }
}
