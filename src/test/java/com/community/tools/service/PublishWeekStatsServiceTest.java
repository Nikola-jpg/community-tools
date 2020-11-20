package com.community.tools.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PublishWeekStatsServiceTest {

  @Test
  void exportStatTest_ChatNameTest() {
    List<EventData> events = new ArrayList<>();
    EventData evet = new EventData(new Date(), "roman", Event.PULL_REQUEST_CLOSED);
    events.add(evet);

    GitHubService gitHubEventService = mock(GitHubService.class);
    SlackService slackService = mock(SlackService.class);
    Mockito.when(gitHubEventService.getEvents(any(), any())).thenReturn(events);

    PublishWeekStatsService taskTestService = new PublishWeekStatsService(gitHubEventService,
        slackService);
    assertDoesNotThrow(() -> {
      taskTestService.exportStat();
      Mockito.verify(slackService).sendBlockMessageToConversation(eq("general"), anyString());
    });
  }

  @Test
  void exportStatTest() {
    String message = "[{\"type\": \"header\",\t\"text\": "
            + "{\"type\": \"plain_text\",\"text\": \"Statistic:\"}},"
            + "{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\", \"text\": \"\n"
            + "*Comment*:loudspeaker::  3\n"
            + "*Pull Request created*:mailbox_with_mail::  2\n"
            + "*Pull Request closed*:moneybag::  1\"\t}]},"
            + "{\"type\": \"header\",\"text\": {\"type\": \"plain_text\",\"text\": \"Activity:\"}},"
            + "{\"type\": \"context\",\n"
            + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"*roman*: "
            + ":loudspeaker::loudspeaker::loudspeaker::mailbox_with_mail:"
            + ":mailbox_with_mail::moneybag:\"}]}]";
    List<EventData> events = Arrays.asList(
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CLOSED),
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED),
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED),
        new EventData(new Date(), "roman", Event.COMMENT),
        new EventData(new Date(), "roman", Event.COMMENT),
        new EventData(new Date(), "roman", Event.COMMENT));

    GitHubService gitHubEventService = mock(GitHubService.class);
    SlackService slackService = mock(SlackService.class);
    Mockito.when(gitHubEventService.getEvents(any(), any())).thenReturn(events);

    PublishWeekStatsService taskTestService = new PublishWeekStatsService(gitHubEventService,
        slackService);
    assertDoesNotThrow(() -> {
      taskTestService.exportStat();
      Mockito.verify(slackService).sendBlockMessageToConversation(anyString(), eq(message));
    });
  }


  @Test
  void exportStatTestTwoAuthors() {
    String message = "[{\"type\": \"header\",\t\"text\": "
            + "{\"type\": \"plain_text\",\"text\": \"Statistic:\"}},"
            + "{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\", \"text\": \"\n"
            + "*Comment*:loudspeaker::  4\n"
            + "*Pull Request created*:mailbox_with_mail::  3\n"
            + "*Commit*:rolled_up_newspaper::  2\n"
            + "*Pull Request closed*:moneybag::  1\"\t}]},"
            + "{\"type\": \"header\",\"text\": {\"type\": \"plain_text\",\"text\": \"Activity:\"}},"
            + "{\"type\": \"context\",\n"
            + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"*roman*:"
            + " :loudspeaker::loudspeaker::loudspeaker::mailbox_with_mail:"
            + ":mailbox_with_mail::moneybag:\"}]},"
            + "{\"type\": \"context\",\n"
            + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"*Ilona*:"
            + " :loudspeaker::mailbox_with_mail::rolled_up_newspaper:"
            + ":rolled_up_newspaper:\"}]}]";


    List<EventData> events = Arrays.asList(
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CLOSED),
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED),
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED),
        new EventData(new Date(), "roman", Event.COMMENT),
        new EventData(new Date(), "roman", Event.COMMENT),
        new EventData(new Date(), "roman", Event.COMMENT),
        new EventData(new Date(), "Ilona", Event.COMMIT),
        new EventData(new Date(), "Ilona", Event.COMMIT),
        new EventData(new Date(), "Ilona", Event.PULL_REQUEST_CREATED),
        new EventData(new Date(), "Ilona", Event.COMMENT)
    );

    GitHubService gitHubEventService = mock(GitHubService.class);
    SlackService slackService = mock(SlackService.class);
    Mockito.when(gitHubEventService.getEvents(any(), any())).thenReturn(events);

    PublishWeekStatsService taskTestService = new PublishWeekStatsService(gitHubEventService,
        slackService);
    assertDoesNotThrow(() -> {
      taskTestService.exportStat();
      Mockito.verify(slackService).sendBlockMessageToConversation(anyString(), eq(message));
    });
  }

  @Test
  void exportStatTestTwoAuthorsNewVersion() {
    String message = "[{\"type\": \"header\",\t\"text\": "
            + "{\"type\": \"plain_text\",\"text\": \"Statistic:\"}},"
            + "{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\", \"text\": \"\n"
            + "*Comment*:loudspeaker::  4\n"
            + "*Commit*:rolled_up_newspaper::  3\n"
            + "*Pull Request created*:mailbox_with_mail::  3\n"
            + "*Pull Request closed*:moneybag::  1\"\t}]},"
            + "{\"type\": \"header\",\"text\": {\"type\": \"plain_text\",\"text\": \"Activity:\"}},"
            + "{\"type\": \"context\",\n"
            + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"*aleksandr-zatsarnui*:"
            + " :loudspeaker::loudspeaker::loudspeaker::rolled_up_newspaper:"
            + ":mailbox_with_mail::mailbox_with_mail::moneybag:\"}]},"
            + "{\"type\": \"context\",\n"
            + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"*NikitaBatalskiy*:"
            + " :loudspeaker::rolled_up_newspaper:"
            + ":rolled_up_newspaper::mailbox_with_mail:\"}]}]";



    List<EventData> events = Arrays.asList(
            new EventData(new Date(), "aleksandr-zatsarnui", Event.PULL_REQUEST_CLOSED),
            new EventData(new Date(), "aleksandr-zatsarnui", Event.PULL_REQUEST_CREATED),
            new EventData(new Date(), "aleksandr-zatsarnui", Event.PULL_REQUEST_CREATED),
            new EventData(new Date(), "aleksandr-zatsarnui", Event.COMMENT),
            new EventData(new Date(), "aleksandr-zatsarnui", Event.COMMENT),
            new EventData(new Date(), "aleksandr-zatsarnui", Event.COMMENT),
            new EventData(new Date(), "aleksandr-zatsarnui", Event.COMMIT),
            new EventData(new Date(), "NikitaBatalskiy", Event.COMMIT),
            new EventData(new Date(), "NikitaBatalskiy", Event.COMMIT),
            new EventData(new Date(), "NikitaBatalskiy", Event.PULL_REQUEST_CREATED),
            new EventData(new Date(), "NikitaBatalskiy", Event.COMMENT)
    );

    GitHubService gitHubEventService = mock(GitHubService.class);
    SlackService slackService = mock(SlackService.class);
    Mockito.when(gitHubEventService.getEvents(any(), any())).thenReturn(events);

    PublishWeekStatsService taskTestService = new PublishWeekStatsService(gitHubEventService,
            slackService);
    assertDoesNotThrow(() -> {
      taskTestService.exportStat();
      Mockito.verify(slackService).sendBlockMessageToConversation(anyString(), eq(message));
    });
  }



}
