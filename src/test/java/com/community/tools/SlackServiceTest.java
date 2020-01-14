package com.community.tools;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class SlackServiceTest {

  public SlackService slackService = mock(SlackService.class);

  @Test
  void sendMessageTest() {

    String actualInputData_username = "roman";
    String actualInputData_messageText = "test message";

    assertDoesNotThrow(() -> {
      slackService.sendPrivateMessage(actualInputData_username, actualInputData_messageText);
      Mockito.verify(slackService).sendPrivateMessage("roman", "test message");
    });
  }
}