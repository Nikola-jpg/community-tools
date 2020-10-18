package com.community.tools.service.github;

import com.community.tools.model.StateEntity;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.io.IOException;
import java.util.List;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHReaction;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class KarmaService {

  @Autowired
  private GitHubConnectService service;
  @Autowired
  private StateEntity stateEntity;
  @Autowired
  private StateMachineRepository stateMachineRepository;
  @Autowired
  private MentorsRepository mentorsRepository;

  public void changeKarmaForCommentApproved(String traineeReviewer, int amountOfKarma) {
    if (stateMachineRepository.findByGitName(traineeReviewer).isPresent()
        && !mentorsRepository.findByGitNick(traineeReviewer).isPresent()) {

      stateEntity = stateMachineRepository.findByGitName(traineeReviewer).get();
      int numberOfKarma = stateEntity.getKarma();
      if (numberOfKarma == 0) {
        stateEntity.setKarma(1);
      } else {
        stateEntity.setKarma(numberOfKarma + amountOfKarma);
      }
      stateMachineRepository.save(stateEntity);
    }
  }

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
      throw new RuntimeException(e);
    }
  }

  private void karmaForTypeOfReaction(GHIssueComment comment, String actorPullRequest) {
    String typeOfReaction;
    String actorOfReaction;
    String usersComment = "";
    try {
      usersComment = comment.getUser().getLogin();
    } catch (IOException e) {
      e.printStackTrace();
    }
    PagedIterable<GHReaction> listOfReaction = comment.listReactions();
    for (GHReaction reaction : listOfReaction) {
      actorOfReaction = reaction.getUser().getLogin();
      typeOfReaction = reaction.getContent().getContent();
      if (typeOfReaction.equals("+1") && !actorOfReaction.equals(usersComment)) {
        if (actorPullRequest.equals(actorOfReaction)) {
          changeKarmaForCommentApproved(usersComment, 1);
        } else if (mentorsRepository.findByGitNick(actorOfReaction).isPresent()) {
          changeKarmaForCommentApproved(usersComment, 2);
        }
      } else if (typeOfReaction.equals("-1")
        /*&& mentorsRepository.findByGitNick(actorOfReaction).isPresent()*/) {
        changeKarmaForCommentApproved(usersComment, -1);
      }
    }
  }
}
