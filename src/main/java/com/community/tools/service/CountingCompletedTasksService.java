package com.community.tools.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHUser;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CountingCompletedTasksService {

  private final GitHubConnectService service;


  public Map<String, List<String>> getCountedCompletedTasks() {
    //Конечный лист
    Map<String, List<String>> listUsers = new HashMap<>();
    try {

      //Создаем список закрыых пулл реквестов
      List<GHPullRequest> pullRequests = service.getGitHubRepository()
          .getPullRequests(GHIssueState.CLOSED);

      //Список закрытых реквесов братухами
      List<GHPullRequest> pullRequestsClosedByBro = new ArrayList<>();
      pullRequests.stream().filter(pr -> {
        try {
          return pr.getLabels().stream().anyMatch(s -> s.getName().equals("done"));
        } catch (IOException e) {
          e.printStackTrace();
        }
        return false;
      })
          .forEach(pullRequestsClosedByBro::add);

      //заполняем конечный список
      for (GHPullRequest pr : pullRequestsClosedByBro) {
        if (listUsers.containsKey(pr.getUser().getLogin())) {
          listUsers.get(pr.getUser().getLogin())
              .add(pr.getTitle());
        } else {
          List<String> tasks = new ArrayList<>();
          tasks.add(pr.getTitle());
          listUsers.put(pr.getUser().getLogin(), tasks);
        }
      }

    } catch (IOException e) {
    throw new RuntimeException(e);
  }
    return listUsers;
  }
}
