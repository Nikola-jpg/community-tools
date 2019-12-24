package com.community.tools.service;

import com.community.tools.SlackService;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import javax.websocket.DeploymentException;
import org.kohsuke.github.GHEventInfo;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service("PRAnnouncer")
public class NewPRAnnouncer {

  @Autowired
  GitHubService gitHubService;

  @Autowired
  SlackService slackService;

  @Value("${slack.username}")
  String userName;

  @Scheduled(cron = "0 0 * * * ?")
  public void prAnnouncement() {
    long currentTime = System.currentTimeMillis();
    List<GHEventInfo> events = null;
    try {
      events = gitHubService.getGitHubConnection().getEvents();

      for (GHEventInfo event : events) {
        long eventTime = event.getCreatedAt().getTime();
        if (timeComparing(currentTime, eventTime)) {
          String eventName = event.getType().name();
          if (eventName.equalsIgnoreCase("ready for review")) {
            slackService.sendMessage(userName,
                event.getActor().getName() + " " + event.getRepository().getName());
          }
        }
      }
      GHRepository repository = gitHubService.getGitHubConnection()
          .getRepository(gitHubService.nameRepository);
      List<GHPullRequest> pullList = repository.getPullRequests(GHIssueState.OPEN);
      for (GHPullRequest pullReq : pullList) {
        long eventTime = pullReq.getCreatedAt().getTime();
        if (timeComparing(currentTime, eventTime)) {
          String prDescription = pullReq.getBody();
          slackService.sendMessage(userName, prDescription);
        }
      }
    } catch (IOException e) {

    } catch (DeploymentException e) {
    } catch (SlackApiException e) {
    }
  }

  public boolean timeComparing(long currentTime, long eventTime) {
    Calendar currentTimeCal = Calendar.getInstance();
    Calendar eventTimeCal = Calendar.getInstance();
    currentTimeCal.setTimeInMillis(currentTime);
    eventTimeCal.setTimeInMillis(eventTime);
    return currentTimeCal.get(Calendar.HOUR_OF_DAY) == eventTimeCal.get(Calendar.HOUR_OF_DAY)
        && currentTimeCal.get(Calendar.DAY_OF_MONTH) == eventTimeCal.get(Calendar.DAY_OF_MONTH)
        && currentTimeCal.get(Calendar.MONTH) == eventTimeCal.get(Calendar.MONTH)
        && currentTimeCal.get(Calendar.YEAR) == eventTimeCal.get(Calendar.YEAR);
  }
}
