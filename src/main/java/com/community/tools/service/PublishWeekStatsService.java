package com.community.tools.service;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.community.tools.model.Messages;
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

import net.dv8tion.jda.api.EmbedBuilder;
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

  @Value("${noActivityMessage}")
  private String noActivityMessage;

  @Autowired
  private MessageService messageService;

  @Autowired
  private BlockService blockService;

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
    EmbedBuilder embedBuilder = new EmbedBuilder();
    if (events.size() == 0) {
      messageService.sendMessageToConversation(channel, Messages.NO_ACTIVITY_MESSAGE);
      System.out.println(events);
    } else {
      Map<String, List<EventData>> sortedMapGroupByActors = new HashMap<>();
      events.stream().filter(ed -> !sortedMapGroupByActors.containsKey(ed.getActorLogin()))
              .forEach(ed -> sortedMapGroupByActors.put(ed.getActorLogin(), new ArrayList<>()));

      embedBuilder.addField("", "`Statistic:`", false);
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
                messageBuilder.append(": ");
                messageBuilder.append(entry.getValue().size());
                embedBuilder.addField("", getTypeTitleBold(entry.getKey())
                    + emojiGen(entry.getKey()) + ": " + entry.getValue().size(), false);
              });
      embedBuilder.addField("", "`Activity:`", false);
      messageBuilder.append("\"\t}]},{\"type\": \"header\",\"text\": "
              + "{\"type\": \"plain_text\",\"text\": \"Activity:\"}}");
      sortedMapGroupByActors.entrySet().stream()
              .sorted(Comparator
                      .comparingInt((Entry<String, List<EventData>> entry)
                          -> entry.getValue().size()).reversed())
              .forEach(name -> {
                StringBuilder authorsActivMessage = new StringBuilder();
                name.getValue()
                        .forEach(eventData -> { authorsActivMessage
                                .append(emojiGen(eventData.getType()));
                        }
                  );

                messageBuilder.append(",{\"type\": \"context\",\n"
                        + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"*");

                messageBuilder.append(name.getKey());
                messageBuilder.append("*: ");
                messageBuilder.append(authorsActivMessage);
                messageBuilder.append("\"}]}");
                embedBuilder.addField("", name.getKey() + ": "
                    + authorsActivMessage,false);
              });
      messageBuilder.append("]");
      messageService.sendBlockMessageToConversation(channel,
          blockService.statisticMessage(messageBuilder, embedBuilder));

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

    messageService.sendBlockMessageToConversation(channel, blockService.ratingMessage(url, img));
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