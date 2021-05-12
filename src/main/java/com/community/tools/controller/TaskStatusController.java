package com.community.tools.controller;

import com.community.tools.dto.UserTasksStatus;
import com.community.tools.service.TasksStatusService;
import com.community.tools.service.UserTasksStatusService;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/tasksstatus")
public class TaskStatusController {

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Autowired
  private TasksStatusService tasksStatusService;

  @Autowired
  private UserTasksStatusService userTasksStatusService;

  List<UserTasksStatus> userTasksStatuses;

  String[] tasksForUsers = {"checkstyle", "primitives", "boxing", "valueref", "equals.hashcode",
      "platform", "bytecode", "gc", "exceptions", "classpath", "generics", "inner.classes",
      "override.overload", "strings", "gamelife"};

  /**
   * This method return webpage with table of status.
   * @param model Model
   * @return webpage with template "tasksstatus"
   */
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String getTaskStatus(Model model) {

    userTasksStatuses = new ArrayList<>();

    List<GHPullRequest> pullRequests = tasksStatusService.getPullRequests(60);

    tasksStatusService.sortedByActors(pullRequests)
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
