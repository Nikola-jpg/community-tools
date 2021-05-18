package com.community.tools.controller;

import com.community.tools.dto.UserTasksStatusDto;
import com.community.tools.model.User;
import com.community.tools.service.PullRequestsService;
import com.community.tools.service.TaskStatusService;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
  private TaskStatusService taskStatusService;

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  /**
   * This method return webpage with table of status.
   * @param model Model
   * @return webpage with template "tasksstatus"
   */
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String getTaskStatus(Model model) {

    List<UserTasksStatusDto> userTasksStatusDtoList = new ArrayList<>();

    List<User> users = stateMachineRepository.findAll();
    users.forEach(user -> {
      userTasksStatusDtoList.add(UserTasksStatusDto.fromUser(user, tasksForUsers));
    });

    model.addAttribute("tasksForUsers", tasksForUsers);
    model.addAttribute("userTasksStatuses", userTasksStatusDtoList);

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

    return "tasksstatus";
  }

  /**
   * Download and save into data base all tasks status.
   * @param response response
   */
  @RequestMapping(value = "/download", method = RequestMethod.GET)
  public void downloadAllTasksStatus(HttpServletResponse response) {
    List<GHPullRequest> ghPullRequests = pullRequestsService.getPullRequests();
    taskStatusService.downloadTasksStatus(ghPullRequests);
    response.getStatus();
  }

}
