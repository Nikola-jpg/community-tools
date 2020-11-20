package com.community.tools.service.github;

import com.community.tools.model.User;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
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
  private User user;
  @Autowired
  private StateMachineRepository stateMachineRepository;
  @Autowired
  private MentorsRepository mentorsRepository;

  /**
   * This method changes karma of trainee after reaction.
   * @param traineeReviewer Github login of trainee
   * @param amountOfKarma amount of karma
   */
  public void changeUserKarma(String traineeReviewer, int amountOfKarma) {
    if (stateMachineRepository.findByGitName(traineeReviewer).isPresent()
        && !mentorsRepository.findByGitNick(traineeReviewer).isPresent()) {

      user = stateMachineRepository.findByGitName(traineeReviewer).get();
      int numberOfKarma = user.getKarma();
      if (numberOfKarma == 0) {
        user.setKarma(1);
      } else {
        user.setKarma(numberOfKarma + amountOfKarma);
      }
      stateMachineRepository.save(user);
    }
  }

  /**
   * This method will change karma, if comment is approved.
   * @param numberOfPull number of pull request
   */
  public void changeKarmaBasedOnReaction(int numberOfPull) {
    try {
      GHRepository repository = service.getGitHubRepository();
      List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.ALL);
      GHPullRequest currentPr = pullRequests.stream()
          .filter(pr -> pr.getNumber() == numberOfPull).findFirst().get();
      String actorPullRequest = currentPr.getUser().getLogin();
      PagedIterable<GHIssueComment> comments = currentPr.listComments();
      boolean approvedComment = comments.asList()
          .stream().anyMatch(c -> c.getBody().toLowerCase().equals("approved"));
      if (approvedComment) {
        GHIssueComment comment = comments.asList().stream()
              .filter(c -> c.getBody().toLowerCase().equals("approved"))
              .findFirst().get();
        karmaForTypeOfReaction(comment, actorPullRequest);
      }

    } catch (IOException e) {
      log.info("Some happen with connection to Gh", e);
      throw new RuntimeException(e);
    }
  }

  private void karmaForTypeOfReaction(GHIssueComment comment, String actorPullRequest) {
    String typeOfReaction;
    String actorOfReaction;
    String actorOfComment;
    try {
      actorOfComment = comment.getUser().getLogin();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    PagedIterable<GHReaction> listOfReaction = comment.listReactions();
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
