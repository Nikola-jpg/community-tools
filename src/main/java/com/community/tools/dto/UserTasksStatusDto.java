package com.community.tools.dto;

import com.community.tools.model.User;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class UserTasksStatusDto {

  private String loginPlatform;

  private String loginGithub;

  private int completedTasks;

  private Map<String, String> taskStatusMap;

  /**
   * Converted user to user dto.
   * @param user user
   * @param tasksForUsers array tasks
   * @return user dto
   */
  public static UserTasksStatusDto fromUser(User user, String[] tasksForUsers) {
    UserTasksStatusDto userTasksStatusDto = new UserTasksStatusDto();
    userTasksStatusDto.setLoginPlatform(user.getSlackLogin());
    userTasksStatusDto.setLoginGithub(user.getGitName());
    userTasksStatusDto.setTaskStatusMap(new HashMap<>());

    Arrays.stream(tasksForUsers).forEach(task -> {
      user.getTaskStatuses().forEach(taskStatus -> {
        if (task.equals(taskStatus.getTaskName())) {
          String viewTaskStatus;
          switch (taskStatus.getTaskStatus()) {
            case "pull request": {
              viewTaskStatus = "pullRequest";
              break;
            }
            case "changes requested": {
              viewTaskStatus = "changesRequest";
              break;
            }
            case "ready for review": {
              viewTaskStatus = "readyForReview";
              break;
            }
            case "done": {
              viewTaskStatus = "done";
              break;
            }
            default: {
              viewTaskStatus = "";
            }
          }
          userTasksStatusDto.getTaskStatusMap().put(task, viewTaskStatus);
          if (viewTaskStatus.equals("done")) {
            userTasksStatusDto.addCompletedTask();
          }
        }

      });
    });
    return userTasksStatusDto;
  }

  public void addCompletedTask() {
    completedTasks++;
  }


}
