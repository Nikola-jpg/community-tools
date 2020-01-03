package com.community.tools.service;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import javax.websocket.DeploymentException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;


@SpringBootTest
class ScheduledTasksTest {
  //@SpyBean
  private ScheduledTasks taskTestService = mock(ScheduledTasks.class);

  @Test
  void exportStatChanel() throws DeploymentException, IOException, SlackApiException {
    assertDoesNotThrow(() -> {
      taskTestService.exportStat("test");
    });
  }

}
