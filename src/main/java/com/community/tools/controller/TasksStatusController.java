package com.community.tools.controller;

import com.community.tools.dto.UserTasksStatusDto;
import com.community.tools.model.User;
import com.community.tools.service.PullRequestsService;
import com.community.tools.service.TaskStatusService;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


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
  public String getTaskStatus(Model model,
      @RequestParam(name = "sortByField", required = false, defaultValue = "gitName")
          String sortByField) {

    List<UserTasksStatusDto> userTasksStatusDtoList = new ArrayList<>();
    Page<User> page = stateMachineRepository.findAll(
        PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, sortByField)));
    //List<User> users = stateMachineRepository.findAll();
    List<User> users = page.getContent();
    users.forEach(user -> {
      userTasksStatusDtoList.add(UserTasksStatusDto.fromUser(user, tasksForUsers));
    });

    model.addAttribute("tasksForUsers", tasksForUsers);
    model.addAttribute("userTasksStatuses", userTasksStatusDtoList);

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

  /**
   * Update from json.
   * @param body json
   * @param response status
   */
  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public void updateTasksStatus(@RequestBody String body, HttpServletResponse response) {
    JSONObject json = new JSONObject(body);
    taskStatusService.updateTasksStatus(json);
    response.getStatus();
  }

}
