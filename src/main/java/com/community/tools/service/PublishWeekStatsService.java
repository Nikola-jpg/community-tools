package com.community.tools.service;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.community.tools.model.Messages;
import com.community.tools.service.github.GitHubService;
import com.github.seratch.jslack.api.methods.SlackApiException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishWeekStatsService {

  @Autowired
  private GitHubService ghEventService;

  @Value("${importantInformationChannel}")
  private String channel;

  @Value("${urlServer}")
  private String urlServer;

  @Autowired
  private MessageService messageService;

  /**
   * Publish statistics of Events for last week. Statistic sends every Monday.
   *
   * @throws SlackApiException SlackApiException
   * @throws IOException       IOException
   */
  @Scheduled(cron = "0 0 0 * * MON")
  public void exportStat()
          throws SlackApiException, IOException {
    Date endDate = new Date();
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -7);
    Date startDate = cal.getTime();

    List<EventData> events = ghEventService.getEvents(startDate, endDate);
    if (events.size() == 0) {
      messageService.sendMessageToConversation(channel, Messages.NO_ACTIVITY_MESSAGE);
      System.out.println(events);
    } else {
      messageService.sendBlockMessageToConversation(channel,
          messageService.statisticMessage(events));
    }
  }

  /**
   * Publish message with link to trainee`s leaderboard and image (first 5 record of rating).
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  @Scheduled(cron = "0 10 0 * * MON")
  public void publishLeaderboard() throws IOException, SlackApiException {
    String url = urlServer + "leaderboard/";
    String date = LocalDate.now().toString();
    String img = url + "img/" + date;

    messageService.sendBlockMessageToConversation(channel,
        messageService.infoLinkMessage(Messages.RATING_MESSAGE, url, img));
  }

  /**
   * Publish message with link to trainee`s tasks status and image (first 5 record of rating).
   */
  @Scheduled(cron = "0 20 0 * * ?")
  public void publishTasksStatus() {
    String url = urlServer + "tasksstatus";
    String date = LocalDate.now().toString();
    String img = url + "img/" + date;

    messageService.sendBlockMessageToConversation(channel,
        messageService.infoLinkMessage(Messages.TASKS_STATUS_MESSAGE, url, img));
  }

  /**
   * Get emoji by event type.
   * @param type event type
   * @return emoji
   */
  public static String emojiGen(Event type) {
    switch (type) {
      case COMMENT:
        return ":loudspeaker:";
      case COMMIT:
        return ":rolled_up_newspaper:";
      case PULL_REQUEST_CLOSED:
        return ":moneybag:";
      case PULL_REQUEST_CREATED:
        return ":mailbox_with_mail:";
      default:
        return "";
    }
  }

  public static String getTypeTitleBold(Event type) {
    String typeTitleBold = "*" + type.getTitle() + "*";
    return typeTitleBold;
  }
}