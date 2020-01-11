package com.community.tools.service;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import com.community.tools.SlackService;
import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;



class PublishWeekStatsServiceTest {

  @Test
  void exportStatTest_ChatNameTest(){
    List<EventData> events = new ArrayList<>();
    EventData evet = new EventData(new Date(), "roman", Event.PULL_REQUEST_CLOSED);
    events.add(evet);

    GitHubEventService gitHubEventService = mock(GitHubEventService.class);
    SlackService slackService = mock(SlackService.class);
    Mockito.when(gitHubEventService.getEvents(any(), any())).thenReturn(events);

    PublishWeekStatsService taskTestService = new PublishWeekStatsService(gitHubEventService, slackService);
    assertDoesNotThrow(() -> {
      taskTestService.exportStat("test");
      Mockito.verify(slackService).sendMessageToChat(eq("test"), anyString());
    });
  }
  @Test
  void exportStatTest(){
    String message = ":construction: ТИПЫ :construction:\n"
        + "COMMENT:loudspeaker: : 3\n"
        + "PULL_REQUEST_CREATED:mailbox_with_mail:: 2\n"
        + "PULL_REQUEST_CLOSED:moneybag:: 1\n"
        + " ----------------------------------------\n"
        + ":construction: АКТИВНОСТЬ :construction:\n"
        + "roman: :loudspeaker: :loudspeaker: :loudspeaker:"
        + " :mailbox_with_mail::mailbox_with_mail::moneybag:\n";
    List<EventData> events = new ArrayList<>();
    EventData evet = new EventData(new Date(), "roman", Event.PULL_REQUEST_CLOSED);
    events.add(evet);
    evet = new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED);
    events.add(evet);
    evet = new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED);
    events.add(evet);
    evet = new EventData(new Date(), "roman", Event.COMMENT);
    events.add(evet);
    evet = new EventData(new Date(), "roman", Event.COMMENT);
    events.add(evet);
    evet = new EventData(new Date(), "roman", Event.COMMENT);
    events.add(evet);


    GitHubEventService gitHubEventService = mock(GitHubEventService.class);
    SlackService slackService = mock(SlackService.class);
    Mockito.when(gitHubEventService.getEvents(any(), any())).thenReturn(events);

    PublishWeekStatsService taskTestService = new PublishWeekStatsService(gitHubEventService, slackService);
    assertDoesNotThrow(() -> {
      taskTestService.exportStat("test");
      Mockito.verify(slackService).sendMessageToChat(anyString(), eq(message));
    });
  }


  @Test
  void exportStatTestTwoAuthors(){
    String message = ":construction: ТИПЫ :construction:\n"
        + "COMMENT:loudspeaker: : 4\n"
        + "PULL_REQUEST_CREATED:mailbox_with_mail:: 3\n"
        + "COMMIT:rolled_up_newspaper:: 2\n"
        + "PULL_REQUEST_CLOSED:moneybag:: 1\n"
        + " ----------------------------------------\n"
        + ":construction: АКТИВНОСТЬ :construction:\n"
        + "roman: :loudspeaker: :loudspeaker: :loudspeaker: :mailbox_with_mail::mailbox_with_mail::moneybag:\n"
        + "Ilona: :loudspeaker: :mailbox_with_mail::rolled_up_newspaper::rolled_up_newspaper:\n";
    List<EventData> events = new ArrayList<>();
    EventData evet = new EventData(new Date(), "roman", Event.PULL_REQUEST_CLOSED);
    events.add(evet);
    evet = new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED);
    events.add(evet);
    evet = new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED);
    events.add(evet);
    evet = new EventData(new Date(), "roman", Event.COMMENT);
    events.add(evet);
    evet = new EventData(new Date(), "roman", Event.COMMENT);
    events.add(evet);
    evet = new EventData(new Date(), "roman", Event.COMMENT);
    events.add(evet);
    evet = new EventData(new Date(), "Ilona", Event.COMMIT);
    events.add(evet);
    evet = new EventData(new Date(), "Ilona", Event.COMMIT);
    events.add(evet);
    evet = new EventData(new Date(), "Ilona", Event.PULL_REQUEST_CREATED);
    events.add(evet);
    evet = new EventData(new Date(), "Ilona", Event.COMMENT);
    events.add(evet);


    GitHubEventService gitHubEventService = mock(GitHubEventService.class);
    SlackService slackService = mock(SlackService.class);
    Mockito.when(gitHubEventService.getEvents(any(), any())).thenReturn(events);

    PublishWeekStatsService taskTestService = new PublishWeekStatsService(gitHubEventService, slackService);
    assertDoesNotThrow(() -> {
      taskTestService.exportStat("test");
      Mockito.verify(slackService).sendMessageToChat(anyString(), eq(message));
    });
  }
}
