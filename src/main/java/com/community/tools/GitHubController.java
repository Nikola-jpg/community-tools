package com.community.tools;

import com.community.tools.service.GitHubService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GitHubController {

  @Autowired
  private GitHubService service;

  @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getHelloInJson() {
    List<String> list = new ArrayList<>();
    list.add("Hello");
    list.add("World");
    return ResponseEntity.ok().body(list);
  }
}
