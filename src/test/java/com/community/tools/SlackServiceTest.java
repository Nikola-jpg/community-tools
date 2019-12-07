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
  
  @Value("${slack.token}")
  private String token;

  private static SlackService slackService;

  @BeforeAll
  public static void initSlackService() {
    slackService = new SlackService();
  }

  @Test
  void sendPrivateMessage() throws IOException, SlackApiException {
    //setup
    String actualInputData_username = "Slackbot";
    String actualInputData_messageText = "test message";
    //execute
    ChatPostMessageResponse response =
            slackService.sendPrivateMessage(actualInputData_username, actualInputData_messageText);
    Slack slack = Slack.getInstance();
    ChatDeleteResponse deleteResponse =
            slack.methods(token).chatDelete(req ->
                    req.channel(response.getChannel()).ts(response.getTs()));
    boolean actualData = deleteResponse.isOk();
    //verify
    assertTrue(actualData);
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
