package com.community.tools.service;

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
   * @throws SlackApiException SlackApiException
   * @throws IOException IOException
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

    messageBuilder.append("[{\"type\": \"header\",\t\"text\": {\"type\":"
            + " \"plain_text\",\"text\": \"Statistic:\"}},"
            + "{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\", \"text\": \"");
    events.stream()
        .collect(Collectors.groupingBy(EventData::getType))
        .entrySet().stream()
        .sorted(Comparator
            .comparingInt((Entry<Event, List<EventData>> entry) -> entry.getValue().size())
            .reversed())
        .forEach(entry -> {
          entry.getValue().forEach(e -> sortedMapGroupByActors.get(e.getActorLogin()).add(e));
          messageBuilder.append("\n");
          messageBuilder.append(getTypeTitleBold(entry.getKey())).append(emojiGen(entry.getKey()));
          messageBuilder.append(":  ");
          messageBuilder.append(entry.getValue().size());
        });
    messageBuilder.append("\"\t}]},{\"type\": \"header\",\"text\": "
            + "{\"type\": \"plain_text\",\"text\": \"Activity:\"}}");
    sortedMapGroupByActors.entrySet().stream()
        .sorted(Comparator
            .comparingInt((Entry<String, List<EventData>> entry) -> entry.getValue().size())
            .reversed())
        .forEach(name -> {
          StringBuilder authorsActivMessage = new StringBuilder();
          name.getValue()
                  .forEach(eventData -> authorsActivMessage.append(emojiGen(eventData.getType())));
          messageBuilder.append(",{\"type\": \"context\",\n"
                  + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"*");
          messageBuilder.append(name.getKey());
          messageBuilder.append("*: ");
          messageBuilder.append(authorsActivMessage);
          messageBuilder.append("\"}]}");
        });
    messageBuilder.append("]");
    slackService.sendBlockMessageToConversation("general", messageBuilder.toString());
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