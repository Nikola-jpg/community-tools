package com.community.tools.service.github;

import com.community.tools.model.User;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

  public void increaseKarmaForCommentApproved(String traineeReviewer) {
    if (stateMachineRepository.findByGitName(traineeReviewer).isPresent()
        && !mentorsRepository.findByGitNick(traineeReviewer).isPresent()){

      user = stateMachineRepository.findByGitName(traineeReviewer).get();
      int numberOfKarma = user.getKarma();
      if (numberOfKarma == 0) {
        user.setKarma(1);
      } else {
        user.setKarma(numberOfKarma + 1);
      }
      stateMachineRepository.save(user);
    }
  }
}
