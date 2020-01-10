package com.community.tools.service;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHEventInfo;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("PRAnnouncer")

public class NewPRAnnouncer {

  @Value("${slack.webhook}")
  private String slackWebHook;

  private final GitHubConnectService service;

  public void sendAnnouncement(String message) {
    try {
      Payload payload = Payload.builder().text(message).build();
      Slack slack = Slack.getInstance();
      slack.send(slackWebHook, payload);
    } catch (IOException e) {

    }
  }

  @Scheduled(cron = "0 0 * * * ?")
  public void prAnnouncement() {

    List<GHEventInfo> events;
    try {
      events = service.getGitHubConnection().getEvents();
      for (GHEventInfo event : events) {
        long eventTime = event.getCreatedAt().getTime();
        if (timeComparing(eventTime)) {
          String eventName = event.getType().name();
          if (eventName.equalsIgnoreCase("ready for review")) {
            sendAnnouncement(event.getActor().getName() + " " + event.getRepository().getName());
          }
        }
      }
      GHRepository repository = service.getGitHubRepository();
      List<GHPullRequest> pullList = repository.getPullRequests(GHIssueState.OPEN);
      for (GHPullRequest pullReq : pullList) {
        long eventTime = pullReq.getCreatedAt().getTime();
        if (timeComparing(eventTime)) {
          String prDescription = pullReq.getBody();
          sendAnnouncement(prDescription);
        }
      }
    } catch (IOException e) {

    }
  }

  public long initialTime() {

    LocalDateTime now = LocalDateTime.now();
    return LocalDateTime.of(now.getYear(), now.getMonthValue()
        , now.getDayOfMonth(), now.getHour(), 0)
        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

  }

  public boolean timeComparing(long eventTime) {

    long deviationTime = eventTime - initialTime();
    return deviationTime < 3_600_000;
  }
}
