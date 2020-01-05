package com.community.tools.service.github;

import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GitHubUsersRepositoriesService {

  private final GitHubConnectService service;

  public Set<String> getGitHubAllUsers() {
    try {
      GHRepository repository = service.getGitHubRepository();
      return repository.getCollaboratorNames();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
