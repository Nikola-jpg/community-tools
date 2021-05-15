package com.community.tools.service;

import com.community.tools.dto.UserTasksStatus;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserTasksStatusService {

  private boolean reverse;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @Autowired
  private PullRequestsService pullRequestsService;

  @Autowired
  private StateMachineService stateMachineService;

  @Autowired
  private MessageService messageService;

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

    userTasksStatus.setLoginPlatform(messageService.getUserById(stateMachineService
        .getIdByNick(loginGithub)));
    Map<String, String> taskStatusMap = new HashMap<>();
    userTasksStatus.setTaskStatus(taskStatusMap);

    Arrays.stream(tasksForUsers).forEach(task -> {
      pullRequestsService.sortedByTitlePullRequest(sortedByActorsPullRequests)
          .forEach((title, sortedByTitlePullRequests) -> {

            if (new LevenshteinDistance().apply(task, title) < 3) {
              sortedByTitlePullRequests.forEach(pullRequest -> {
                try {
                  String viewTaskStatus;
                  String lastLabel = pullRequest.getLabels().isEmpty() ? "pull request" :
                      pullRequest.getLabels().stream().findFirst().get().getName();
                  switch (lastLabel) {
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
                  taskStatusMap.put(task, viewTaskStatus);
                  if (lastLabel.equals("done")) {
                    userTasksStatus.addCompletedTask();
                  }
                } catch (IOException exception) {
                  throw new RuntimeException(exception);
                }
              });
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
