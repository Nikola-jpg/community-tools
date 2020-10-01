package com.community.tools.controller;

import com.community.tools.model.EventData;
import com.community.tools.service.CountingCompletedTasksService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
public class GitHubController {
  private final SlackService slackService;
  private final GitHubService gitHubService;
  private final CountingCompletedTasksService completedTasksService;

  @ApiOperation(value = "Test endpoint that returns list ['Hello', 'World']")
  @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getHelloInJson() {
    List<String> list = new ArrayList<>();
    list.add("Hello");
    list.add("World");
    return ok().body(list);
  }

  @ApiOperation(value = "Returns map of 'username: pull request title'")
  @ApiImplicitParam(name = "state", dataType = "boolean", paramType = "path",
    required = true, value = "'true' returns opened pull requests, 'false' - closed")
  @GetMapping(value = "/pull_request/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Map<String, String>>> getPullRequests(@PathVariable boolean state) {
    Map<String, String> userPullRequest = gitHubService.getPullRequests(state);
    List<Map<String, String>> list = new ArrayList<>();
    list.add(userPullRequest);
    return ok().body(list);
  }

  @ApiOperation(value = "Returns map 'username: [pull request title]'"
          + " of closed pull requests with 'done' label")
  @GetMapping(value = "/pull_request/сlosedReq", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, List<String>>> getPullRequests() throws IOException {
    Map<String, List<String>> map = completedTasksService.getCountedCompletedTasks();
    return ok().body(map);
  }

  @ApiOperation(value = "Returns list of pull requests, comments"
          + " and commits in the defined interval")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "start", required = true,
                  value = "Date in format 'yyyy-MM-dd'"),
          @ApiImplicitParam(name = "end", required = true,
                  value = "Date in format 'yyyy-MM-dd'")
  })
  @GetMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<EventData>> getAllEvents(
      @RequestParam(name = "start") String startDate,
      @RequestParam(name = "end") String endDate) throws ParseException {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date start = format.parse(startDate);
    Date end = format.parse(endDate);

    List<EventData> eventData = gitHubService.getEvents(start, end);
    return ok().body(eventData);
  }
}
