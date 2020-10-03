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
            + "{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\", \"text\": \">>>\n"
            + "COMMENT:loudspeaker:: 3\n"
            + "PULL_REQUEST_CREATED:mailbox_with_mail:: 2\n"
            + "PULL_REQUEST_CLOSED:moneybag:: 1\"\t}]},"
            + "{\"type\": \"header\",\"text\": {\"type\": \"plain_text\",\"text\": \"Activity:\"}},"
            + "{\"type\": \"context\",\n"
            + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"`roman:` \"\n"
            + "\t\t\t}\n"
            + "\t\t]\n"
            + "\t},\n"
            + "\t{\n"
            + "\t\t\"type\": \"context\",\n"
            + "\t\t\"elements\": [\n"
            + "\t\t\t{\n"
            + "\t\t\t\t\"type\": \"mrkdwn\",\n"
            + "\t\t\t\t\"text\": \">>>:mailbox_with_mail::    3  \\n :loudspeaker::    4  \\n"
            + " :moneybag::    2  \\n \"}]}]";
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
            + "{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\", \"text\": \">>>\n"
            + "COMMENT:loudspeaker:: 4\n"
            + "PULL_REQUEST_CREATED:mailbox_with_mail:: 3\n"
            + "COMMIT:rolled_up_newspaper:: 2\n"
            + "PULL_REQUEST_CLOSED:moneybag:: 1\"\t}]},{\"type\": \"header\",\"text\": "
            + "{\"type\": \"plain_text\",\"text\": \"Activity:\"}},{\"type\": \"context\",\n"
            + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"`roman:` \"\n"
            + "\t\t\t}\n"
            + "\t\t]\n"
            + "\t},\n"
            + "\t{\n"
            + "\t\t\"type\": \"context\",\n"
            + "\t\t\"elements\": [\n"
            + "\t\t\t{\n"
            + "\t\t\t\t\"type\": \"mrkdwn\",\n"
            + "\t\t\t\t\"text\": \">>>:mailbox_with_mail::    3  \\n :loudspeaker::    4  \\n"
            + " :moneybag::    2  \\n \"}]},"
            + "{\"type\": \"context\",\n"
            + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"`Ilona:` \"\n"
            + "\t\t\t}\n"
            + "\t\t]\n"
            + "\t},\n"
            + "\t{\n"
            + "\t\t\"type\": \"context\",\n"
            + "\t\t\"elements\": [\n"
            + "\t\t\t{\n"
            + "\t\t\t\t\"type\": \"mrkdwn\",\n"
            + "\t\t\t\t\"text\": \">>>:rolled_up_newspaper::    3  \\n"
            + " :mailbox_with_mail::    2  \\n"
            + " :loudspeaker::    2  \\n \"}]}]";
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
            + "{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\", \"text\": \">>>\n"
            + "COMMENT:loudspeaker:: 4\n"
            + "COMMIT:rolled_up_newspaper:: 3\n"
            + "PULL_REQUEST_CREATED:mailbox_with_mail:: 3\n"
            + "PULL_REQUEST_CLOSED:moneybag:: 1\"\t}]},"
            + "{\"type\": \"header\",\"text\": {\"type\": \"plain_text\",\"text\": \"Activity:\"}},"
            + "{\"type\": \"context\",\n"
            + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"`aleksandr-zatsarnui:` \"\n"
            + "\t\t\t}\n"
            + "\t\t]\n"
            + "\t},\n"
            + "\t{\n"
            + "\t\t\"type\": \"context\",\n"
            + "\t\t\"elements\": [\n"
            + "\t\t\t{\n"
            + "\t\t\t\t\"type\": \"mrkdwn\",\n"
            + "\t\t\t\t\"text\": \">>>:rolled_up_newspaper::    2  \\n"
            + " :mailbox_with_mail::    3  \\n"
            + " :loudspeaker::    4  \\n :moneybag::    2  \\n \"}]},{\"type\": \"context\",\n"
            + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"`NikitaBatalskiy:` \"\n"
            + "\t\t\t}\n"
            + "\t\t]\n"
            + "\t},\n"
            + "\t{\n"
            + "\t\t\"type\": \"context\",\n"
            + "\t\t\"elements\": [\n"
            + "\t\t\t{\n"
            + "\t\t\t\t\"type\": \"mrkdwn\",\n"
            + "\t\t\t\t\"text\": \">>>:rolled_up_newspaper::    3  \\n"
            + " :mailbox_with_mail::    2  \\n"
            + " :loudspeaker::    2  \\n \"}]}]";
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
