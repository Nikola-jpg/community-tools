package com.community.tools.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitHubPullRequestService {

  @Autowired
  GitHubConnectService service;

  public Map<String, String> getPullRequests(boolean statePullRequest) {
    Map<String, String> listUsers = new HashMap<>();
    try {
      GHRepository repository = service.getGitHubRepository();
      List<GHPullRequest> pullRequests;
      if (!statePullRequest) {
        pullRequests = repository.getPullRequests(GHIssueState.CLOSED);
      } else {
        pullRequests = repository.getPullRequests(GHIssueState.OPEN);
      }

      for (GHPullRequest repo : pullRequests) {
        String login = repo.getUser().getLogin();
        String title = repo.getTitle();
        listUsers.put(login, title);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return listUsers;
  }
}
