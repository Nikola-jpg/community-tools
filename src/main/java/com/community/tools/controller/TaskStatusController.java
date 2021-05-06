package com.community.tools.controller;

import static com.community.tools.util.GetServerAddress.getAddress;

import com.community.tools.dto.UserTaskStatus;
import com.community.tools.model.User;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHEventInfo;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHEventPayload.Create;
import org.kohsuke.github.GHEventPayload.Issue;
import org.kohsuke.github.GHEventPayload.IssueComment;
import org.kohsuke.github.GHEventPayload.PullRequest;
import org.kohsuke.github.GHEventPayload.PullRequestReview;
import org.kohsuke.github.GHEventPayload.PullRequestReviewComment;
import org.kohsuke.github.GHPullRequestReviewEvent;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/taskstatus")
public class TaskStatusController {

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Autowired
  private GitHubConnectService gitHubConnectService;

  @Autowired
  private GitHubService gitHubService;

  List<UserTaskStatus> taskStatusList;

  String[] tasksForUsers = {"checkstyle", "primitives", "boxing", "valueref", "equals.hashcode",
      "platform", "bytecode", "gc", "exceptions", "classpath", "generics", "inner.classes",
      "override.overload", "strings", "gamelife"};

  /**
   * This method return webpage with table of status.
   * @param model Model
   * @return webpage with template "taskstatus"
   */
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String getTaskStatus(Model model) {
    Map<String, String> taskStatus = Arrays.stream(tasksForUsers).collect(Collectors.toMap(key -> key, value -> ""));
    taskStatusList = new ArrayList<>();
    Map<GHUser, UserTaskStatus> ghUserUserTaskStatusMap = new HashMap<>();

    try {
      List<GHEventInfo> ghEventInfoList = gitHubConnectService.getGitHubRepository().listEvents().asList();

      for (GHEventInfo ghEventInfo: ghEventInfoList) {

        if (ghEventInfo.getType().name().equalsIgnoreCase("ISSUE_COMMENT")) {
          UserTaskStatus userTaskStatus = new UserTaskStatus();
          userTaskStatus.setTaskStatus(new HashMap<>(taskStatus));

          if (!ghEventInfo.getPayload(IssueComment.class).getIssue().getLabels().isEmpty()) {
            userTaskStatus.getTaskStatus().put(ghEventInfo.getPayload(IssueComment.class)
                .getIssue().getTitle(), ghEventInfo.getPayload(IssueComment.class)
                .getIssue().getLabels().stream().findFirst().get()
                .getName().replaceAll("\\s+",""));
          }
          GHUser ghUser = ghEventInfo.getPayload(IssueComment.class).getIssue().getUser();
          userTaskStatus.setLoginGithub(ghUser.getLogin());
          taskStatusList.add(userTaskStatus);
        }
      }
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }

    model.addAttribute("taskList", tasksForUsers);
    model.addAttribute("taskStatusList", taskStatusList);

    return "taskstatus";
  }
/*
  @RequestMapping(value = "/{sortedField}", method = RequestMethod.GET)
  public String getTaskStatusSorted(Model model, @PathVariable("sortedField") String sortedField ) {
    switch (sortedField) {
      case "platformName": {
        taskStatusList.sort(Comparator.comparing(UserTaskStatus::getLoginPlatform).reversed());
        break;
      }
      case "gitName": {
        taskStatusList.sort(Comparator.comparing(UserTaskStatus::getLoginGithub).reversed());
        break;
      }
      case "completedTask": {
        taskStatusList.sort(Comparator.comparing(UserTaskStatus::getCompletedTasks).reversed());
        break;
      }
    }

    model.addAttribute("taskStatusList", taskStatusList);
    model.addAttribute("taskList", tasksForUsers);
  return "taskstatus";
  }
*/
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
