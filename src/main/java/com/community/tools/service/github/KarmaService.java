package com.community.tools.service.github;

import com.community.tools.model.GitHubComment;
import com.community.tools.model.User;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestReviewComment;
import org.kohsuke.github.GHReaction;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KarmaService {

  @Autowired
  private GitHubConnectService service;
  @Autowired
  private StateMachineRepository stateMachineRepository;
  @Autowired
  private MentorsRepository mentorsRepository;

  /**
   * This method will increase karma, if comment is approved.
   *
   * @param traineeReviewer Github login of trainee
   * @param amountOfKarma   amount of karma
   */
  public void changeUserKarma(String traineeReviewer, int amountOfKarma) {
    if (stateMachineRepository.findByGitName(traineeReviewer).isPresent()
            && !mentorsRepository.findByGitNick(traineeReviewer).isPresent()) {
      User user = stateMachineRepository.findByGitName(traineeReviewer).get();
      user.setKarma(user.getKarma() + amountOfKarma);
      stateMachineRepository.save(user);
    }
  }

  /**
   * This method will change karma, if comment is approved.
   *
   * @param numberOfPull number of pull request
   */
  public void changeKarmaBasedOnReaction(int numberOfPull) {
    try {
      GHRepository repository = service.getGitHubRepository();
      GHPullRequest currentPr = repository.getPullRequest(numberOfPull);
      String actorPullRequest = currentPr.getUser().getLogin();

      List<GitHubComment> comments = new ArrayList<>();
      PagedIterable<GHIssueComment> commentsIssue = currentPr.listComments();
      checkForIssueCommentsApproved(commentsIssue, comments);
      PagedIterable<GHPullRequestReviewComment> commentsReview = currentPr.listReviewComments();
      checkForReviewsCommentsApproved(commentsReview, comments);

      comments.stream().sorted(Comparator.comparing(GitHubComment::getCreatedAt))
              .collect(Collectors.groupingBy(GitHubComment::getAuthorComment))
              .values().stream().map(c -> c.get(0))
              .forEach(c -> karmaForReaction(c, actorPullRequest));
    } catch (IOException e) {
      log.info("Some happen with connection to Gh", e);
      throw new RuntimeException(e);
    }
  }

  private void checkForIssueCommentsApproved(PagedIterable<GHIssueComment> commentsIssue,
                                             List<GitHubComment> comments) throws IOException {
    List<GHIssueComment> issueCommentList = commentsIssue.asList().stream()
            .filter(c -> c.getBody().toLowerCase().equals("approved")).collect(Collectors.toList());

    for (GHIssueComment issueComment: issueCommentList) {
      GitHubComment comment = new GitHubComment();
      comment.setAuthorComment(issueComment.getUser().getLogin());
      comment.setCreatedAt(issueComment.getCreatedAt());
      comment.setListOfReaction(issueComment.listReactions());
      comments.add(comment);
    }
  }

  private void checkForReviewsCommentsApproved(PagedIterable<GHPullRequestReviewComment> reviews,
                                               List<GitHubComment> comments) throws IOException {
    List<GHPullRequestReviewComment> reviewCommentsList = reviews.asList().stream()
            .filter(c -> c.getBody().toLowerCase().equals("approved")).collect(Collectors.toList());

    for (GHPullRequestReviewComment reviewComment: reviewCommentsList) {
      GitHubComment comment = new GitHubComment();
      comment.setAuthorComment(reviewComment.getUser().getLogin());
      comment.setCreatedAt(reviewComment.getCreatedAt());
      comment.setListOfReaction(reviewComment.listReactions());
      comments.add(comment);
    }
  }

  private void karmaForReaction(GitHubComment comment, String actorPullRequest) {
    String typeOfReaction;
    String actorOfReaction;
    String actorOfComment = comment.getAuthorComment();

    PagedIterable<GHReaction> listOfReaction = comment.getListOfReaction();
    for (GHReaction reaction : listOfReaction) {
      actorOfReaction = reaction.getUser().getLogin();
      typeOfReaction = reaction.getContent().getContent();
      if (typeOfReaction.equals("+1") && !actorOfReaction.equals(actorOfComment)) {
        if (actorPullRequest.equals(actorOfReaction)) {
          changeUserKarma(actorOfComment, 1);
        } else if (mentorsRepository.findByGitNick(actorOfReaction).isPresent()) {
          changeUserKarma(actorOfComment, 2);
        }
      } else if (typeOfReaction.equals("-1")
              && mentorsRepository.findByGitNick(actorOfReaction).isPresent()) {
        changeUserKarma(actorOfComment, -1);
      }
    }
  }
}
