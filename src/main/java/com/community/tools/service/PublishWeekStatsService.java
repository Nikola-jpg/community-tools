package com.community.tools.service;

import static com.community.tools.util.GetServerAddress.getAddress;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.github.seratch.jslack.api.methods.SlackApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishWeekStatsService {

  private final GitHubService ghEventService;
  private final SlackService slackService;

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
    StringBuilder messageBuilder = new StringBuilder();

    Map<String, List<EventData>> sortedMapGroupByActors = new HashMap<>();
    events.stream().filter(ed -> !sortedMapGroupByActors.containsKey(ed.getActorLogin()))
            .forEach(ed -> sortedMapGroupByActors.put(ed.getActorLogin(), new ArrayList<>()));

    messageBuilder.append(":construction: ТИПЫ :construction:");
    events.stream()
            .collect(Collectors.groupingBy(EventData::getType))
            .entrySet().stream()
            .sorted(Comparator
                    .comparingInt((Entry<Event, List<EventData>> entry) -> entry.getValue().size())
                    .reversed())
            .forEach(entry -> {
              entry.getValue().forEach(e -> sortedMapGroupByActors.get(e.getActorLogin()).add(e));
              messageBuilder.append("\n");
              messageBuilder.append(entry.getKey()).append(emojiGen(entry.getKey()));
              messageBuilder.append(": ");
              messageBuilder.append(entry.getValue().size());
            });
    messageBuilder.append("\n ----------------------------------------");
    messageBuilder.append("\n").append(":construction: АКТИВНОСТЬ :construction:\n");
    sortedMapGroupByActors.entrySet().stream()
            .sorted(Comparator
                    .comparingInt((Entry<String, List<EventData>> entry) -> entry.getValue().size())
                    .reversed())
            .forEach(name -> {
              StringBuilder authorsActivMessage = new StringBuilder();
              name.getValue()
                      .forEach(eventData -> authorsActivMessage
                              .append(emojiGen(eventData.getType())));

              messageBuilder.append(name.getKey());
              messageBuilder.append(": ");
              messageBuilder.append(authorsActivMessage);
              messageBuilder.append("\n");
            });

    slackService.sendMessageToConversation("test_3", messageBuilder.toString());
  }

  /**
   * Publish message with link to trainee`s leaderboard and image (first 5 record of rating).
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  @Scheduled(cron = "0 0 0 * * MON")
  public void publishLeaderboard() throws IOException, SlackApiException {
    StringBuilder sb = new StringBuilder();
    String url = getAddress();
    sb.append("{\"blocks\": [{\"type\": \"section\", \"text\": ");
    sb.append("{\"type\": \"mrkdwn\",\"text\": \"Рейтинг этой недели доступен по ссылке: \"},");
    sb.append("\"accessory\": {\"type\": \"button\",\t\"text\": ");
    sb.append("{\"type\": \"plain_text\",\"text\": \":loudspeaker:\",\"emoji\": true},");
    sb.append("\"value\": \"click_me_123\", \"url\": \"");
    sb.append(url);
    sb.append("\", \"action_id\": \"button-action\"}},{\"type\": \"image\",\"image_url\": \"");
    sb.append(url + "best/\",\"alt_text\": \"inspiration\"}]}");
    slackService.sendBlockMessageToConversation("general", sb.toString());
  }

  private String emojiGen(Event type) {
    switch (type) {
      case COMMENT:
        return ":loudspeaker: ";
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
}