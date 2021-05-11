package com.community.tools.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserTaskStatus {

  private String loginPlatform;

  private String loginGithub;

  private int completedTasks;

  private Map<String, String> taskStatus;

  public void addCompletedTask() {
    completedTasks++;
  }


}
