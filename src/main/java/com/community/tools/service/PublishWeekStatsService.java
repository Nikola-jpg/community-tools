package com.community.tools.service;

import com.community.tools.SlackService;
import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import lombok.RequiredArgsConstructor;


@EnableScheduling
@RequiredArgsConstructor
public class PublishWeekStatsService {

  private final GitHubEventService ghEventService;
  private final SlackService slackService;

  @Scheduled(cron = "0 0 8 ? * MON *")
  public void exportStat(String chat)
      throws SlackApiException, IOException {

    Date endDate = new Date();
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -7);
    Date startDate = cal.getTime();

    List<EventData> events = ghEventService.getEvents(startDate, endDate);
    StringBuilder messageBuilder = new StringBuilder();

    Map<Event, List<EventData>> typeMap;
    typeMap = events.stream().collect(Collectors.groupingBy(EventData::getType));

    messageBuilder.append(":construction: ТИПЫ :construction:");
    List<Event> sortedEventList = new ArrayList<>();
    typeMap.entrySet().stream()
        .sorted(Comparator
            .comparingInt((Entry<Event, List<EventData>> entry) -> entry.getValue().size())
            .reversed())
        .forEach(entry -> {
          sortedEventList.add(entry.getKey());
          messageBuilder.append("\n");
          messageBuilder.append(entry.getKey()).append(emojiGen(entry.getKey()));
          messageBuilder.append(": ");
          messageBuilder.append(entry.getValue().size());
        });
    messageBuilder.append("\n ----------------------------------------");

    Map<String, List<EventData>> mapGroupByActors = events.stream()
        .collect(Collectors.groupingBy(EventData::getActorLogin));

    messageBuilder.append("\n").append(":construction: АКТИВНОСТЬ :construction:\n");
    mapGroupByActors.entrySet().stream()
        .sorted(Comparator
            .comparingInt((Entry<String, List<EventData>> entry) -> entry.getValue().size())
            .reversed())
        .forEach(name -> {
          StringBuilder authorsActivMessage = new StringBuilder();
          sortedEventList.forEach(event ->
              mapGroupByActors.get(name.getKey()).stream().filter(e -> event.equals(e.getType()))
                  .forEach(eventData -> authorsActivMessage.append(emojiGen(eventData.getType()))));
          messageBuilder.append(name.getKey());
          messageBuilder.append(": ");
          messageBuilder.append(authorsActivMessage);
          messageBuilder.append("\n");
        });

    slackService.sendMessageToChat(chat, messageBuilder.toString());
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