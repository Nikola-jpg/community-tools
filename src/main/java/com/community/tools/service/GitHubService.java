package com.community.tools.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitHubService {

  @Value("${token}")
  public String token;

  @Value("${repository}")
  public String nameRepository;

  public Map<String, String> getPullRequests(boolean statePullRequest) {
    Map<String, String> listUsers = new HashMap<>();
    try {
      GitHub gitHub = getGitHubConnection();
      GHRepository repository = gitHub.getRepository(nameRepository);

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

  public GitHub getGitHubConnection() {
    GitHub gitHub;
    try {
      gitHub = GitHub.connectUsingOAuth(token);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return gitHub;
  }
}
