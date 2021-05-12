package com.community.tools.service;

import com.community.tools.dto.UserTasksStatus;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTasksStatusService {

  private boolean reverse;

  @Autowired
  private TasksStatusService tasksStatusService;

  /**
   * Get tasks status for users.
   * @param loginGithub user login github
   * @param sortedByActorsPullRequests list pull requests sorted by actor
   * @return tasks status for user
   */
  public UserTasksStatus getUserTasksStatus(String loginGithub,
      List<GHPullRequest> sortedByActorsPullRequests) {
    UserTasksStatus userTasksStatus = new UserTasksStatus();
    userTasksStatus.setLoginGithub(loginGithub);
    //userTaskStatus.setLoginPlatform();
    Map<String, String> taskStatus = new HashMap<>();
    userTasksStatus.setTaskStatus(taskStatus);

    tasksStatusService.sortedByTitlePullRequest(sortedByActorsPullRequests)
        .forEach((pullRequestTitle, sortedByTitlePullRequests) -> {
          sortedByTitlePullRequests.forEach(pullRequest -> {
            try {
              String lastLabel = pullRequest.getLabels().isEmpty() ? "pull request" :
                  pullRequest.getLabels().stream().findFirst().get().getName();
              taskStatus.put(pullRequest.getTitle(),lastLabel.replaceAll("\\s+",""));
              if (lastLabel.equals("done")) {
                userTasksStatus.addCompletedTask();
              }
            } catch (IOException exception) {
              throw new RuntimeException(exception);
            }
          });
        });

    return userTasksStatus;
  }

  /**
   * Sorted list user tasks status by field.
   * @param sortedField field for sorted
   * @param userTasksStatusList tasks status list
   */
  public void sortedByField(String sortedField, List<UserTasksStatus> userTasksStatusList) {
    switch (sortedField) {
      case "platformName": {
        if (reverse) {
          userTasksStatusList.sort(Comparator.comparing(UserTasksStatus::getLoginPlatform)
              .reversed());
          reverse = false;
        } else {
          userTasksStatusList.sort(Comparator.comparing(UserTasksStatus::getLoginPlatform));
          reverse = true;
        }
        break;
      }
      case "gitName": {
        if (reverse) {
          userTasksStatusList.sort(Comparator.comparing(UserTasksStatus::getLoginGithub)
              .reversed());
          reverse = false;
        } else {
          userTasksStatusList.sort(Comparator.comparing(UserTasksStatus::getLoginGithub));
          reverse = true;
        }
        break;
      }
      case "completedTask": {
        if (reverse) {
          userTasksStatusList.sort(Comparator.comparing(UserTasksStatus::getCompletedTasks)
              .reversed());
          reverse = false;
        } else {
          userTasksStatusList.sort(Comparator.comparing(UserTasksStatus::getCompletedTasks));
          reverse = true;
        }
        break;
      }
      default: {

      }
    }
  }

}
