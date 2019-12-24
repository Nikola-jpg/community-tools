package com.community.tools.service;

import com.community.tools.model.Event;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;
import org.kohsuke.github.GHPullRequestReviewComment;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitHubEventService {

  @Autowired
  GitHubConnectService service;

  public List<Event> getEvents(Date startDate, Date endDate) {
    try {
      List<Event> list = new ArrayList<>();
      GHRepository repository = service.getGitHubRepository();

      getPullRequests(startDate, endDate, list, repository);

      return list;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void getPullRequests(Date startDate, Date endDate, List<Event> list,
      GHRepository repository) throws IOException {
    List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.ALL);
    for (GHPullRequest pullRequest : pullRequests) {
      Date createdAt = pullRequest.getCreatedAt();
      String actorLogin = pullRequest.getUser().getLogin();
      String state = "PullRequest: " + pullRequest.getState().name();

      if (createdAt.before(endDate) && createdAt.after(startDate)) {
        list.add(new Event(createdAt, actorLogin, state));
        getCommits(list, pullRequest, actorLogin);
        getComments(list, pullRequest);
      }
    }
  }

  private void getCommits(List<Event> list, GHPullRequest pullRequest, String actorLogin) {
    PagedIterable<GHPullRequestCommitDetail> pullRequestCommit = pullRequest.listCommits();
    for (GHPullRequestCommitDetail commit : pullRequestCommit) {
      Date date = commit.getCommit().getAuthor().getDate();
      String message = "Commit: " + commit.getCommit().getMessage();

      list.add(new Event(date, actorLogin, message));
    }
  }

  private void getComments(List<Event> list, GHPullRequest pullRequest) throws IOException {
    PagedIterable<GHPullRequestReviewComment> reviewComments = pullRequest.listReviewComments();
    for (GHPullRequestReviewComment comment : reviewComments) {
      Date createdComment = comment.getCreatedAt();
      String loginComment = comment.getUser().getLogin();
      String bodyComment = "Comment: " + comment.getBody();

      list.add(new Event(createdComment, loginComment, bodyComment));
    }
  }
}

