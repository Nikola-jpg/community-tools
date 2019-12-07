package com.community.tools;

import com.github.seratch.jslack.api.methods.SlackApiException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.websocket.DeploymentException;
import java.io.IOException;

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
