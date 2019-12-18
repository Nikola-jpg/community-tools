package com.community.tools.controller;

import static org.springframework.http.ResponseEntity.*;

import com.community.tools.service.GitHubService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitHubController {

  @Autowired
  private GitHubService service;

  @GetMapping(value = "/hi", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getHelloInJson() {
    List<String> list = new ArrayList<>();
    list.add("Hello");
    list.add("World");
    return ok().body(list);
  }

  @GetMapping(value = "/pull_request/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Map<String, String>>> getPullRequests(@PathVariable boolean state) {
    Map<String, String> userPullRequest = service.getPullRequests(state);
    List<Map<String, String>> list = new ArrayList<>();
    list.add(userPullRequest);
    return ok().body(list);
  }
}
