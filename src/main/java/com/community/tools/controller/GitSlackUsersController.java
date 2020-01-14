package com.community.tools.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
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
import org.springframework.web.bind.annotation.RequestMapping;
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
}
