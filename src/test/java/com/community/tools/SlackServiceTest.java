package com.community.tools;

import com.github.seratch.jslack.api.methods.SlackApiException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.websocket.DeploymentException;
import java.io.IOException;

class SlackServiceTest {

  private static SlackService slackService;

  @BeforeAll
  public static void initSlackService() {
    slackService = new SlackService();
  }

  @Test
  void sendPrivateMessage() {

  }

  @Test
  void sendMessage() {

  }
}