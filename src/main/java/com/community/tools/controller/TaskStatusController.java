package com.community.tools.controller;

import static com.community.tools.util.GetServerAddress.getAddress;

import com.community.tools.dto.UserTaskStatus;
import com.community.tools.service.TaskStatusService;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
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


@Controller
@RequestMapping("/tasksstatus")
public class TaskStatusController {

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Autowired
  private TaskStatusService taskStatusService;

  List<UserTaskStatus> userTaskStatuses;
  private boolean platformNameReversed;
  private boolean gitNameReversed;
  private boolean completedTaskReversed;

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

    userTaskStatuses = new ArrayList<>();

    List<GHPullRequest> pullRequests = taskStatusService.getPullRequests(60);

    taskStatusService.sortedByActors(pullRequests)
        .forEach((loginGithub, sortedByActorsPullRequests) -> {
          UserTaskStatus userTaskStatus = new UserTaskStatus();
          userTaskStatus.setLoginGithub(loginGithub);
          //userTaskStatus.setLoginPlatform();

          Map<String, String> taskStatus = new HashMap<>();
          userTaskStatus.setTaskStatus(taskStatus);

          taskStatusService.sortedByTitlePullRequest(sortedByActorsPullRequests)
              .forEach((pullRequestTitle, sortedByTitlePullRequests) -> {
                sortedByTitlePullRequests.forEach(pullRequest -> {
                  try {
                    String lastLabel = pullRequest.getLabels().isEmpty() ? "pull request" :
                        pullRequest.getLabels().stream().findFirst().get().getName();
                    taskStatus.put(pullRequest.getTitle(),lastLabel.replaceAll("\\s+",""));
                    if (lastLabel.equals("done")) {
                      userTaskStatus.addCompletedTask();
                    }
                  } catch (IOException exception) {
                    throw new RuntimeException(exception);
                  }
                });
              });
          userTaskStatuses.add(userTaskStatus);
        });

    model.addAttribute("tasksForUsers", tasksForUsers);
    model.addAttribute("userTaskStatuses", userTaskStatuses);

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
    switch (sortedField) {
      case "platformName": {
        if (platformNameReversed) {
          userTaskStatuses.sort(Comparator.comparing(UserTaskStatus::getLoginPlatform).reversed());
        } else {
          userTaskStatuses.sort(Comparator.comparing(UserTaskStatus::getLoginPlatform));
          platformNameReversed = true;
        }
        break;
      }
      case "gitName": {
        if (gitNameReversed) {
          userTaskStatuses.sort(Comparator.comparing(UserTaskStatus::getLoginGithub).reversed());
        } else {
          userTaskStatuses.sort(Comparator.comparing(UserTaskStatus::getLoginGithub));
          gitNameReversed = true;
        }
        break;
      }
      case "completedTask": {
        if (completedTaskReversed) {
          userTaskStatuses.sort(Comparator.comparing(UserTaskStatus::getCompletedTasks).reversed());
        } else {
          userTaskStatuses.sort(Comparator.comparing(UserTaskStatus::getCompletedTasks));
          completedTaskReversed = true;
        }
        break;
      }
      default: {

      }
    }

    model.addAttribute("userTaskStatuses", userTaskStatuses);
    model.addAttribute("tasksForUsers", tasksForUsers);
    return "tasksstatus";
  }

  /**
   * This method return image with table, which contains first 5 trainees of leaderboard.
   * @param response HttpServletResponse
   * @throws EntityNotFoundException EntityNotFoundException
   * @throws IOException IOException
   */
  /*
  @RequestMapping(value = "/img/{date}", method = RequestMethod.GET)
  public void getImage(HttpServletResponse response) throws EntityNotFoundException, IOException {
    String url = getAddress();
    byte[] data = leaderBoardService.createImage(url);
    response.setContentType(MediaType.IMAGE_PNG_VALUE);
    response.getOutputStream().write(data);
    response.setContentLength(data.length);
  }
*/

}
