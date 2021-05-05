package com.community.tools.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Hryhorii Perets
 */
@Data
@AllArgsConstructor
public class UserTaskStatus {

  private String loginPlatform;

  private String loginGithub;

  private int completedTasks;

  private Map<String, String> taskStatus;


}
