package com.community.tools.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.community.tools.service.github.GitHubUsersRepositoriesService;
import com.community.tools.service.slack.SlackUsersService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class GitSlackUsersController {

  private final SlackUsersService usersService;
  private final GitHubUsersRepositoriesService gitUsersService;

  @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Set<String>> getAllUsers(@RequestParam(name = "app") String app) {
    if (app.equals("git")) {
      Set<String> gitHubAllUsers = gitUsersService.getGitHubAllUsers();
      return ok().body(gitHubAllUsers);
    }

    if (app.equals("slack")) {
      Set<String> allUsers = usersService.getAllUsers();
      return ok().body(allUsers);
    }
    throw new RuntimeException("you enter wrong app");
  }
}
