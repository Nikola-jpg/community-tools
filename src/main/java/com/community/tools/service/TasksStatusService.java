package com.community.tools.service;

import com.community.tools.service.github.GitHubConnectService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TasksStatusService {

  private final GitHubConnectService gitHubConnectService;
  private final MessageService messageService;

  @Value("${importantInformationChannel}")
  private String channel;

  @Value("${noActivityMessage}")
  private String noActivityMessage;

  /**
   * Get sorted map group by actors.
   * Key is actors name, value are list pull requests.
   * @return sorted map
   */
  public Map<String, List<GHPullRequest>> sortedByActors(List<GHPullRequest> pullRequests) {
    Map<String, List<GHPullRequest>> sortedMapGroupByActors = new HashMap<>();

    pullRequests.stream().filter(ed -> {
      try {
        return !sortedMapGroupByActors
            .containsKey(ed.getUser().getLogin());
      } catch (IOException exception) {
        throw new RuntimeException(exception);
      }
    })
        .forEach(ed -> {
          try {
            sortedMapGroupByActors.put(ed.getUser().getLogin(), new ArrayList<>());
          } catch (IOException exception) {
            throw new RuntimeException(exception);
          }
        });

    pullRequests.stream().forEach(ed -> {
      try {
        sortedMapGroupByActors.get(ed.getUser().getLogin()).add(ed);
      } catch (IOException exception) {
        throw new RuntimeException(exception);
      }
    });

    return sortedMapGroupByActors;
  }

  /**
   * Get sorted map group by tasks.
   * Key is task title, value are list pull requests.
   * @return sorted map
   */
  public Map<String, List<GHPullRequest>>
      sortedByTitlePullRequest(List<GHPullRequest> pullRequests) {
    Map<String, List<GHPullRequest>> sortedMapGroupByTitles = new HashMap<>();

    pullRequests.stream().filter(ed -> !sortedMapGroupByTitles.containsKey(ed.getTitle()))
        .forEach(ed -> sortedMapGroupByTitles.put(ed.getTitle(), new ArrayList<>()));

    pullRequests.stream().forEach(ed -> sortedMapGroupByTitles.get(ed.getTitle()).add(ed));

    return sortedMapGroupByTitles;
  }

  /**
   * Get all pull requests.
   * @return all pull requests
   */
  public List<GHPullRequest> getPullRequests() {
    List<GHPullRequest> pullRequests;
    try {
      pullRequests = gitHubConnectService.getGitHubRepository().getPullRequests(GHIssueState.ALL);
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    return pullRequests;
  }

  /**
   * Get pull requests for the last days.
   * @param lastDays number
   * @return list pull requests
   */
  public List<GHPullRequest> getPullRequests(int lastDays) {
    List<GHPullRequest> pullRequests;

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -lastDays);
    Date startDate = cal.getTime();
    try {
      pullRequests = gitHubConnectService.getGitHubRepository().getPullRequests(GHIssueState.ALL);

    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    return pullRequests.stream().filter(pullRequest -> {
      try {
        return pullRequest.getCreatedAt().after(startDate);
      } catch (IOException exception) {
        throw new RuntimeException(exception);
      }
    }).collect(Collectors.toList());
  }
}