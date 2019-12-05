package com.community.tools.service;

import com.community.tools.config.ConfigProperties;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitHubService {

  @Autowired
  private ConfigProperties properties;

  public Map<String, String> getPullRequests(boolean statePullRequest) {
    Map<String, String> listUsers = new HashMap<>();
    try {
      GitHub gitHub = getGitHubConnection();
      String nameRepository = properties.getRepository();
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
      e.printStackTrace();
    }
    return listUsers;
  }

  private GitHub getGitHubConnection() {
    GitHub gitHub = null;
    try {
      String token = properties.getToken();
      gitHub = GitHub.connectUsingOAuth(token);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return gitHub;
  }
}
