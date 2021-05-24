package com.community.tools.service;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.community.tools.service.github.GitHubService;
import com.github.seratch.jslack.api.methods.SlackApiException;

import java.io.IOException;
import java.time.LocalDate;
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishWeekStatsService {

  private final GitHubService ghEventService;
  private final MessageService messageService;

  @Value("${importantInformationChannel}")
  private String channel;

  @Value("${urlServer}")
  private String urlServer;

  @Value("${noActivityMessage}")
  private String noActivityMessage;

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
    if (events.size() == 0) {
      messageService.sendMessageToConversation(channel, noActivityMessage);
      System.out.println(events);
    } else {
      Map<String, List<EventData>> sortedMapGroupByActors = new HashMap<>();
      events.stream().filter(ed -> !sortedMapGroupByActors.containsKey(ed.getActorLogin()))
              .forEach(ed -> sortedMapGroupByActors.put(ed.getActorLogin(), new ArrayList<>()));

      messageBuilder.append("[{\"type\": \"header\",\t\"text\": {\"type\":"
              + " \"plain_text\",\"text\": \"Statistic:\"}},"
              + "{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\", \"text\": \"");
      events.stream()
              .collect(Collectors.groupingBy(EventData::getType))
              .entrySet().stream()
              .sorted(Comparator
                      .comparingInt((Entry<Event, List<EventData>> entry)
                          -> entry.getValue().size()).reversed())
              .forEach(entry -> {
                entry.getValue().forEach(e -> sortedMapGroupByActors.get(e.getActorLogin()).add(e));
                messageBuilder.append("\n");
                messageBuilder.append(getTypeTitleBold(entry.getKey()))
                        .append(emojiGen(entry.getKey()));
                messageBuilder.append(":  ");
                messageBuilder.append(entry.getValue().size());
              });
      messageBuilder.append("\"\t}]},{\"type\": \"header\",\"text\": "
              + "{\"type\": \"plain_text\",\"text\": \"Activity:\"}}");
      sortedMapGroupByActors.entrySet().stream()
              .sorted(Comparator
                      .comparingInt((Entry<String, List<EventData>> entry)
                          -> entry.getValue().size()).reversed())
              .forEach(name -> {
                StringBuilder authorsActivMessage = new StringBuilder();
                name.getValue()
                        .forEach(eventData -> authorsActivMessage
                                .append(emojiGen(eventData.getType())));
                messageBuilder.append(",{\"type\": \"context\",\n"
                        + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"*");
                messageBuilder.append(name.getKey());
                messageBuilder.append("*: ");
                messageBuilder.append(authorsActivMessage);
                messageBuilder.append("\"}]}");
              });
      messageBuilder.append("]");
      messageService.sendBlockMessageToConversation(channel, messageBuilder.toString());
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
    String message = String.format("[{\"type\": \"section\", \"text\": "
            + "{\"type\": \"mrkdwn\",\"text\": \"Рейтинг этой недели доступен по ссылке: \"},"
            + "\"accessory\": {\"type\": \"button\",\t\"text\": "
            + "{\"type\": \"plain_text\",\"text\": \":loudspeaker:\",\"emoji\": true},"
            + "\"value\": \"click_me_123\", \"url\": \"%s"
            + "\", \"action_id\": \"button-action\"}},{\"type\": \"image\",\"image_url\": \"%s"
            + "\",\"alt_text\": \"inspiration\"}]", url, img);

    messageService.sendBlockMessageToConversation(channel, message);
  }

  /**
   * Publish message with link to trainee`s tasks status and image (first 5 record of rating).
   */
  @Scheduled(cron = "0 20 0 * * ?")
  public void publishTasksStatus() {
    String url = urlServer + "tasksstatus";
    String date = LocalDate.now().toString();
    String img = url + "img/" + date;
    String message = String.format("[{\"type\": \"section\", \"text\": "
        + "{\"type\": \"mrkdwn\",\"text\": \"Прогресс выполнения задач доступен по ссылке: \"},"
        + "\"accessory\": {\"type\": \"button\",\t\"text\": "
        + "{\"type\": \"plain_text\",\"text\": \":loudspeaker:\",\"emoji\": true},"
        + "\"value\": \"click_me_123\", \"url\": \"%s"
        + "\", \"action_id\": \"button-action\"}},{\"type\": \"image\",\"image_url\": \"%s"
        + "\",\"alt_text\": \"inspiration\"}]", url, img);
    messageService.sendBlockMessageToConversation(channel, message);
  }

  private String emojiGen(Event type) {
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

  private String getTypeTitleBold(Event type) {
    String typeTitleBold = "*" + type.getTitle() + "*";
    return typeTitleBold;
  }
}