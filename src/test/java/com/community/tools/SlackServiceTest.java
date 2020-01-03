package com.community.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

@SpringBootTest
class SlackServiceTest {

  //@Autowired
  public SlackService slackService = mock(SlackService.class);

  @Test
  void sendMessageTest() {
    //setup
    String actualInputData_username = "roman";
    String actualInputData_messageText = "test message";
    //execute
    //verify
    assertDoesNotThrow(() -> {
      slackService.sendPrivateMessage(actualInputData_username, actualInputData_messageText);
    });
  }
}