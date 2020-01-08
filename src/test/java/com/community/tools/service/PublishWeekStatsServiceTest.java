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


@SpringBootTest
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
    String message = "\n"
        + ":construction: ТИПЫ :construction:\n"
        + "PULL_REQUEST_CLOSED:moneybag:: 1\n"
        + "COMMIT:rolled_up_newspaper: : 0\n"
        + "PULL_REQUEST_CREATED:mailbox_with_mail:: 0\n"
        + "COMMENT:loudspeaker: : 0\n"
        + " ----------------------------------------\n"
        + ":construction: АКТИВНОСТЬ :construction:\n"
        + "roman: :moneybag::1\n";
    List<EventData> events = new ArrayList<>();
    EventData evet = new EventData(new Date(), "roman", Event.PULL_REQUEST_CLOSED);
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
