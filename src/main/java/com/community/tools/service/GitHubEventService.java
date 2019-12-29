package com.community.tools.service;

import static com.community.tools.model.Event.COMMENT;
import static com.community.tools.model.Event.COMMIT;
import static com.community.tools.model.Event.PULL_REQUEST_CLOSED;
import static com.community.tools.model.Event.PULL_REQUEST_CREATED;
import static java.util.Comparator.comparing;
import static org.kohsuke.github.GHIssueState.CLOSED;
import static org.kohsuke.github.GHIssueState.OPEN;

import com.community.tools.model.EventData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;
import org.kohsuke.github.GHPullRequestReviewComment;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GitHubEventService {

  private final GitHubConnectService service;

  public List<EventData> getEvents(Date startDate, Date endDate) {
    try {
      GHRepository repository = service.getGitHubRepository();
      Set<EventData> listEvents = new TreeSet<>(comparing(EventData::getCreatedAt));

      List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.ALL);
      for (GHPullRequest pullRequest : pullRequests) {
        Date createdAt = pullRequest.getCreatedAt();
        Date closedAt = pullRequest.getClosedAt();
        String actorPullRequest = pullRequest.getUser().getLogin();
        GHIssueState state = pullRequest.getState();

        boolean period = createdAt.after(startDate) && createdAt.before(endDate);
        if (period) {
          if (state == OPEN) {
            listEvents.add(new EventData(createdAt, actorPullRequest, PULL_REQUEST_CREATED));
          }
          if (state == CLOSED) {
            listEvents.add(new EventData(closedAt, actorPullRequest, PULL_REQUEST_CLOSED));
          }
        }

        PagedIterable<GHPullRequestReviewComment> comments = pullRequest.listReviewComments();
        for (GHPullRequestReviewComment comment : comments) {
          Date commentCreatedAt = comment.getCreatedAt();
          String loginComment = comment.getUser().getLogin();
          boolean periodComment =
              commentCreatedAt.after(startDate) && commentCreatedAt.before(endDate);
          if (periodComment) {
            listEvents.add(new EventData(commentCreatedAt, loginComment, COMMENT));
          }
        }

        PagedIterable<GHPullRequestCommitDetail> commits = pullRequest.listCommits();
        for (GHPullRequestCommitDetail commit : commits) {
          Date dateCommit = commit.getCommit().getAuthor().getDate();

          boolean periodCommit = dateCommit.after(startDate) && dateCommit.before(endDate);
          if (periodCommit) {
            listEvents.add(new EventData(dateCommit, actorPullRequest, COMMIT));
          }
        }
      }

      return new ArrayList<>(listEvents);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

