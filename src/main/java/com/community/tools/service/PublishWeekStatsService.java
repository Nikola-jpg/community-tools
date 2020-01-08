package com.community.tools.service;

import com.community.tools.SlackService;
import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import lombok.RequiredArgsConstructor;


@EnableScheduling
@RequiredArgsConstructor
public class PublishWeekStatsService {

  private final GitHubEventService ghEventService;
  private final SlackService slackService;

  private final String[] emoji = {":loudspeaker: ", ":rolled_up_newspaper: ", ":moneybag:",
      ":mailbox_with_mail:"};

  @Scheduled(cron = "0 0 8 ? * MON *")
  public void exportStat(String chat)
      throws SlackApiException, IOException {

    //текущая дата, и дата семи дневной давности
    Date endDate = new Date();
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -7);
    Date startDate = cal.getTime();

    List<EventData> events = ghEventService.getEvents(startDate, endDate);
    StringBuilder messageBuilder = new StringBuilder();

    final String TYPES = ":construction: ТИПЫ :construction:";
    final String AUTHORS = ":construction: АКТИВНОСТЬ :construction:";
    //получение листа типов
    Event[] types = Event.values();

    //получение мапы: общее количество действий каждого типа
    Map<String, String> typesCount = new HashMap<>();
    for (Event s : types) {
      long i = events.stream().filter(e -> s.equals(e.getType()))
          .count();
      typesCount.put(s.toString(), String.valueOf(i));
    }

    //отсортировать мапу типов по значению
    typesCount = sortByValue(typesCount);
    //построить сообщение типов
    messageBuilder.append("\n").append(TYPES);
    for (Entry<String, String> entry : typesCount.entrySet()) {
      messageBuilder.append("\n");
      messageBuilder.append(entry.getKey()).append(emojiGen(entry.getKey()));
      messageBuilder.append(": ");
      messageBuilder.append(entry.getValue());
    }
    messageBuilder.append("\n ----------------------------------------");

    //получение листа пользоваелей
    List<String> authors = new LinkedList<>();
    events.stream().filter(e -> !authors.contains(e.getActorLogin()))
        .forEach(e -> authors.add(e.getActorLogin()));

    //мапа пользоваелей с событиями
    Map<String, Integer> sortAuthors = new HashMap<>();
    Map<String, String> authorsCount = new HashMap<>();
    for (String s : authors) {
      List<EventData> eb = events.stream().filter(e -> e.getActorLogin().equals(s))
          .collect(Collectors.toList());

      StringBuilder str = new StringBuilder();
      Set<String> set = typesCount.keySet();
      for (String event : set) {
        eb.stream().filter(eventData -> eventData.getType().name().equals(event))
            .forEach(eventData -> str.append(emojiGen(eventData.getType().toString())));
      }
      str.append(":").append(eb.size());
      sortAuthors.put(s, eb.size());
      authorsCount.put(s, str.toString());
    }

    sortAuthors = sortByValue(sortAuthors);
    //сортировка авторов через sortAuthors
    messageBuilder.append("\n").append(AUTHORS);
    for (String s : sortAuthors.keySet()) {
      messageBuilder.append("\n");
      messageBuilder.append(s);
      messageBuilder.append(": ");
      messageBuilder.append(authorsCount.get(s));
      messageBuilder.append("\n");
    }

    //отправить сообщение messageBuilder, в канал chat
    slackService.sendMessageToChat(chat, messageBuilder.toString());
  }

  private <K, V extends Comparable<? super V>> Map<K, V>
  sortByValue(Map<K, V> map) {
    Map<K, V> result = new LinkedHashMap<>();
    map.entrySet().stream()
        .sorted(Map.Entry.<K, V>comparingByValue().reversed())
        .forEach(e -> result.put(e.getKey(), e.getValue()));

    return result;
  }

  private String emojiGen(String type) {
    switch (type) {
      case "COMMENT":
        return emoji[0];
      case "COMMIT":
        return emoji[1];
      case "PULL_REQUEST_CLOSED":
        return emoji[2];
      case "PULL_REQUEST_CREATED":
        return emoji[3];
      default:
        return "";
    }
  }
}

