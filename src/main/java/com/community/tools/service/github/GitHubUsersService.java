package com.community.tools.service.github;

import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GitHubUsersService {

  private final GitHubConnectService service;

  public Set<GHUser> getGitHubAllUsers() {
    try {
      GHRepository repository = service.getGitHubRepository();
      return repository.getCollaborators();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
