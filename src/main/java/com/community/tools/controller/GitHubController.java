package com.community.tools.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.community.tools.model.EventData;
import com.community.tools.service.CountingCompletedTasksService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class GitHubController {
  private final SlackService slackService;
  private final GitHubService gitHubService;
  private final CountingCompletedTasksService completedTasksService;

  @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getHelloInJson() {
    List<String> list = new ArrayList<>();
    list.add("Hello");
    list.add("World");
    return ok().body(list);
  }

  @GetMapping(value = "/pull_request/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Map<String, String>>> getPullRequests(@PathVariable boolean state) {
    Map<String, String> userPullRequest = gitHubService.getPullRequests(state);
    List<Map<String, String>> list = new ArrayList<>();
    list.add(userPullRequest);
    return ok().body(list);
  }

  @GetMapping(value = "/pull_request/сlosedReq", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, List<String>>> getPullRequests() throws IOException {
    Map<String, List<String>> map = completedTasksService.getCountedCompletedTasks();
    return ok().body(map);
  }

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

  @GetMapping(value = "/sendTestMessage", produces = MediaType.APPLICATION_JSON_VALUE)
  public void getAllEvents() throws ParseException {
    String message = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"image\",\n"
        + "\t\t\"title\": {\n"
        + "\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\"text\": \"image1\",\n"
        + "\t\t\t\"emoji\": true\n"
        + "\t\t},\n"
        + "\t\t\"image_url\": \"https://api.slack.com/img/blocks/bkb_template_images/beagle.png\",\n"
        + "\t\t\"alt_text\": \"image1\"\n"
        + "\t},\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"Read and confirm that you agree to our <https://www.youtube.com/watch?v=O6YzU00oack|rules> BOY :v:. \"\n"
        + "\t\t},\n"
        + "\t\t\"accessory\": {\n"
        + "\t\t\t\"type\": \"button\",\n"
        + "\t\t\t\"text\": {\n"
        + "\t\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\t\"text\": \"Agree\",\n"
        + "\t\t\t\t\"emoji\": true\n"
        + "\t\t\t},\n"
        + "\t\t\t\"value\": \"click_me_123\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";
    try {
      slackService.sendEventsMessage("roman", message);
    } catch (IOException | SlackApiException e) {
      e.printStackTrace();
    }
  }

  @GetMapping(value = "/sendTestMessage2", produces = MediaType.APPLICATION_JSON_VALUE)
  public void getSendMessage() throws ParseException {
    String message = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"divider\"\n"
        + "\t},\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"This is not work button. \"\n"
        + "\t\t},\n"
        + "\t\t\"accessory\": {\n"
        + "\t\t\t\"type\": \"button\",\n"
        + "\t\t\t\"text\": {\n"
        + "\t\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\t\"text\": \"Button\",\n"
        + "\t\t\t\t\"emoji\": true\n"
        + "\t\t\t},\n"
        + "\t\t\t\"value\": \"click_me_123\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";
    try {
      slackService.sendEventsMessage("roman", message);
    } catch (IOException | SlackApiException e) {
      e.printStackTrace();
    }
  }
}
