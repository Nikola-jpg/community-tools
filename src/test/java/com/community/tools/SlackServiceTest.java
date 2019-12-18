package com.community.tools;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.response.chat.ChatDeleteResponse;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlackServiceTest {

  private static SlackService slackService;

  @BeforeAll
  public static void initSlackService() {
    slackService = new SlackService();
  }

  @Test
  void sendMessageTest() {
    //setup
    String actualInputData_username = "Slackbot";
    String actualInputData_messageText = "test message";
    //execute
    //verify
    assertDoesNotThrow(() -> {
      slackService.sendMessage(actualInputData_username, actualInputData_messageText);
    });
  }
}
