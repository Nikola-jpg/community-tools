package com.community.tools.service.github;

import com.community.tools.model.StateEntity;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

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

  public void increaseKarmaForCommentApproved(String traineeReviewer) {
    if (stateMachineRepository.findByGitName(traineeReviewer).isPresent()
        && !mentorsRepository.findByGitNick(traineeReviewer).isPresent()){

      stateEntity = stateMachineRepository.findByGitName(traineeReviewer).get();
      int numberOfKarma = stateEntity.getKarma();
      if (numberOfKarma == 0) {
        stateEntity.setKarma(1);
      } else {
        stateEntity.setKarma(numberOfKarma + 1);
      }
      stateMachineRepository.save(stateEntity);
    }
  }
}
