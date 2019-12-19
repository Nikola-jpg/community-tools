package com.community.tools.service;

import java.io.IOException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitHubConnectService {

  @Value("${token}")
  private String token;

  @Value("${repository}")
  private String nameRepository;

  public GitHub getGitHubConnection() {
    GitHub gitHub;
    try {
      gitHub = GitHub.connectUsingOAuth(token);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return gitHub;
  }

  public GHRepository getGitHubRepository() {
    GHRepository repository;
    try {
      repository = getGitHubConnection().getRepository(nameRepository);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return repository;
  }
}
