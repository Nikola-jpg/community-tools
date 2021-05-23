package com.community.tools.controller;

import static com.community.tools.util.GetServerAddress.getAddress;

import com.community.tools.dto.UserTasksStatusDto;
import com.community.tools.model.User;
import com.community.tools.service.PullRequestsService;
import com.community.tools.service.TaskStatusService;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
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
          String sortByField,
      @RequestParam(name = "sortDirection", required = false, defaultValue = "asc")
          String sortDirection) {

    List<UserTasksStatusDto> userTasksStatusDtoList = new ArrayList<>();

    Page<User> page = taskStatusService.findAll(1, sortByField, sortDirection);
    List<User> users = page.getContent();
    users.forEach(user -> {
      userTasksStatusDtoList.add(UserTasksStatusDto.fromUser(user, tasksForUsers));
    });

    model.addAttribute("tasksForUsers", tasksForUsers);
    model.addAttribute("userTasksStatuses", userTasksStatusDtoList);
    model.addAttribute("sortDirection", sortDirection);
    String reverseSortDirection = sortDirection.equals("asc") ? "desc" : "asc";
    model.addAttribute("reverseSortDirection", reverseSortDirection);

    return "tasksstatus";
  }

  /**
   * Download and save into data base all tasks status.
   * @param response response
   */
  @RequestMapping(value = "/download", method = RequestMethod.GET)
  public void downloadAllTasksStatus(HttpServletResponse response) {
    List<GHPullRequest> ghPullRequests = pullRequestsService.getPullRequests();
    taskStatusService.cleanBootTasksStatus(ghPullRequests);
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

  /**
   * This method return image with table, which contains first 5 trainees of leaderboard.
   * @param response HttpServletResponse
   * @throws EntityNotFoundException EntityNotFoundException
   * @throws IOException IOException
   */
  @RequestMapping(value = "/img/{date}", method = RequestMethod.GET)
  public void getImage(HttpServletResponse response) throws EntityNotFoundException, IOException {
    String url = getAddress();
    byte[] data = taskStatusService.createImage(url);
    response.setContentType(MediaType.IMAGE_PNG_VALUE);
    response.getOutputStream().write(data);
    response.setContentLength(data.length);
  }

}
