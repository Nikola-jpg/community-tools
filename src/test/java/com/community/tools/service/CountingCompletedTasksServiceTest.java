package com.community.tools.service;


import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CountingCompletedTasksServiceTest {


  @Test
  void getCountedCompletedTasks() throws IOException {

    List<GHPullRequest> fakeReq = new ArrayList<>();
    GHPullRequest pr = mock(GHPullRequest.class);

    Collection<GHLabel> colLabel = new ArrayList<>();
    GHLabel ghLabel = mock(GHLabel.class);
    colLabel.add(ghLabel);
    Mockito.when(ghLabel.getName()).thenReturn("done");
    Mockito.when(pr.getLabels()).thenReturn(colLabel);

    GHUser user = mock(GHUser.class);
    Mockito.when(user.getLogin()).thenReturn("roman");
    Mockito.when(pr.getUser()).thenReturn(user);

    Mockito.when(pr.getTitle()).thenReturn("exercise_1");
    fakeReq.add(pr);

    GitHubConnectService service = mock(GitHubConnectService.class);
    GHRepository rep = mock(GHRepository.class);
    Mockito.when(service.getGitHubRepository()).thenReturn(rep);
    Mockito.when(rep.getPullRequests(GHIssueState.CLOSED)).thenReturn(fakeReq);

    CountingCompletedTasksService cct = new CountingCompletedTasksService(service);

    Map<String, List<String>> map = cct.getCountedCompletedTasks();

    assertEquals(map.size(), 1);
    for (Entry<String, List<String>> list : map.entrySet()) {
      assertEquals(list.getKey(), "roman");
      assertEquals(list.getValue().size(), 1);
      assertEquals(list.getValue().iterator().next(), "exercise_1");
    }
  }
}