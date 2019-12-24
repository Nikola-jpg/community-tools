package com.community.tools.controller;

import static org.springframework.http.ResponseEntity.*;

import com.community.tools.model.Event;
import com.community.tools.service.GitHubEventService;
import com.community.tools.service.GitHubPullRequestService;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
  private GitHubEventService eventService;

  @Autowired
  private GitHubPullRequestService pullRequestService;

  @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getHelloInJson() {
    List<String> list = new ArrayList<>();
    list.add("Hello");
    list.add("World");
    return ok().body(list);
  }

  @GetMapping(value = "/pull_request/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Map<String, String>>> getPullRequests(@PathVariable boolean state) {
    Map<String, String> userPullRequest = pullRequestService.getPullRequests(state);
    List<Map<String, String>> list = new ArrayList<>();
    list.add(userPullRequest);
    return ok().body(list);
  }

  @GetMapping(value = "/event/{startDate}/{endDate}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Event>> getAllEvents(
      @PathVariable String startDate, @PathVariable String endDate) throws ParseException {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date start = format.parse(startDate);
    Date end = format.parse(endDate);
    List<Event> events = eventService.getEvents(start, end);
    return ok().body(events);
  }
}
