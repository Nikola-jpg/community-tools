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
import org.json.JSONException;
import org.json.JSONObject;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

  public Page<User> findAll(int pageNumber, String sortByField, String sortDirection) {
    Sort sort = Sort.by(sortByField);
    sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(pageNumber - 1, 100, sort);
    return stateMachineRepository.findAll(pageable);
  }

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
            String status = pullRequestsService.getLastLabel(ghPullRequest);
            if (!(taskStatus == null)) {
              update(taskStatus, status);
            } else {
              create(user, task, status);
            }
          }
        });
      }
    });
  }

  /**
   * Update task status after pull request.
   * @param json json
   */
  public void updateTasksStatus(JSONObject json) {
    String gitName;
    String title;
    String status;
    try {
      gitName = json.getJSONObject("pull_request").getJSONObject("user").getString("login");
      title = json.getJSONObject("pull_request").getString("title");
      if (json.getJSONObject("pull_request").getJSONArray("labels").isEmpty()) {
        status = "pull request";
      } else {
        status = json.getJSONObject("pull_request").getJSONArray("labels")
            .getJSONObject(0).getString("name");
      }
    } catch (JSONException exception) {
      throw new RuntimeException(exception);
    }
    User user = stateMachineRepository.findByGitName(gitName).orElse(null);
    if (!(user == null)) {
      Arrays.stream(tasksForUsers).forEach(task -> {
        if (new LevenshteinDistance().apply(task, title) < 3) {
          TaskStatus taskStatus = taskStatusRepository
              .findTaskStatusByUserAndTaskName(user, task).orElse(null);
          if (!(taskStatus == null)) {
            update(taskStatus, status);
          } else {
            create(user, task, status);
          }
        }
      });
    }
  }
}
