package com.community.tools.service;

import com.community.tools.model.TaskStatus;
import com.community.tools.model.User;
import com.community.tools.repository.TaskStatusRepository;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @Autowired
  private PullRequestsService pullRequestsService;

  @Autowired
  private TaskStatusRepository taskStatusRepository;

  @Autowired
  private StateMachineRepository stateMachineRepository;

  /**
   * Create new task status.
   * @param user user
   * @param taskName task name
   * @param taskStatus task status
   * @return new task status
   */
  public TaskStatus create(User user, String taskName, String taskStatus) {
    TaskStatus newTaskStatus = new TaskStatus();
    newTaskStatus.setCreated(new Date());
    newTaskStatus.setUser(user);
    newTaskStatus.setTaskName(taskName);
    newTaskStatus.setTaskStatus(taskStatus);
    taskStatusRepository.save(newTaskStatus);
    return newTaskStatus;
  }

  /**
   * Update task status.
   * @param taskStatus task status
   * @param newStatus new status
   * @return updated task status
   */
  public TaskStatus update(TaskStatus taskStatus, String newStatus) {
    taskStatus.setUpdated(new Date());
    taskStatus.setTaskStatus(newStatus);
    taskStatusRepository.save(taskStatus);
    return taskStatus;
  }

  /**
   * Save into data base users task status.
   * @param ghPullRequests github pull requests
   */
  public void downloadTasksStatus(List<GHPullRequest> ghPullRequests) {
    ghPullRequests.forEach(ghPullRequest -> {
      String gitName;
      try {
        gitName = ghPullRequest.getUser().getLogin();
      } catch (IOException exception) {
        throw new RuntimeException(exception);
      }
      User user = stateMachineRepository.findByGitName(gitName).orElse(null);
      if (!(user == null)) {
        String title = ghPullRequest.getTitle();
        Arrays.stream(tasksForUsers).forEach(task -> {
          if (new LevenshteinDistance().apply(task, title) < 3) {
            TaskStatus taskStatus = taskStatusRepository
                .findTaskStatusByUserAndTaskName(user, task).orElse(null);
            if (!(taskStatus == null)) {
              String status = pullRequestsService.getLastLabel(ghPullRequest);
              update(taskStatus, status);
            } else {
              String status = pullRequestsService.getLastLabel(ghPullRequest);
              create(user, task, status);
            }
          }
        });
      }
    });
  }
}
