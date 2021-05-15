package com.community.tools.controller;

import com.community.tools.dto.UserTasksStatus;
import com.community.tools.service.PullRequestsService;
import com.community.tools.service.UserTasksStatusService;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/tasksstatus")
public class TasksStatusController {

  @Autowired
  private PullRequestsService pullRequestsService;

  @Autowired
  private UserTasksStatusService userTasksStatusService;

  private List<UserTasksStatus> userTasksStatuses;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  /**
   * This method return webpage with table of status.
   * @param model Model
   * @return webpage with template "tasksstatus"
   */
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String getTaskStatus(Model model) {

    userTasksStatuses = new ArrayList<>();

    List<GHPullRequest> pullRequests = pullRequestsService.getPullRequests(45);

    pullRequestsService.sortedByActors(pullRequests)
        .forEach((loginGithub, sortedByActorsPullRequests) -> userTasksStatuses.add(
            userTasksStatusService.getUserTasksStatus(loginGithub, sortedByActorsPullRequests)));

    model.addAttribute("tasksForUsers", tasksForUsers);
    model.addAttribute("userTasksStatuses", userTasksStatuses);

    return "tasksstatus";
  }

  /**
   * his method return sorted webpage with table of status.
   * @param model Model
   * @param sortedField value for sorted
   * @return sorted webpage "tasksstatus"
   */
  @RequestMapping(value = "/{sortedField}", method = RequestMethod.GET)
  public String getTaskStatusSorted(Model model, @PathVariable("sortedField") String sortedField) {

    userTasksStatusService.sortedByField(sortedField, userTasksStatuses);

    model.addAttribute("tasksForUsers", tasksForUsers);
    model.addAttribute("userTasksStatuses", userTasksStatuses);

    return "tasksstatus";
  }

}
