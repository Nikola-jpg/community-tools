package com.community.tools.service;

import com.community.tools.model.User;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PointsTaskService {

  @Value("${abilityReviewMessage}")
  private String abilityReviewMessage;

  @Value("${zeroPointsMessage}")
  private String zeroPointsMessage;

  @Value("#{${pointsForTask}}")
  private Map<String, Integer> pointsForTask;

  @Autowired
  private SlackService slackService;

  @Autowired
  private CountingCompletedTasksService countService;

  @Autowired
  MentorsRepository mentorsRepository;

  @Autowired
  StateMachineRepository stateMachineRepository;

  final int numberPullsAbilityReview = 3;



  /**
   * This method adds points to the trainee, when menntor labeled pull as "done".
   If pull has wrong name, add 0 points
   * @param mentor GitNick of person, who add label "done" to  pull request
   * @param creator GitNick of person, who pull request
   * @param pullName Pull request title
   */
  public void addPointForCompletedTask(String mentor, String creator, String pullName) {
    if (mentorsRepository.findByGitNick(mentor).isPresent()) {
      User stateEntity = stateMachineRepository.findByGitName(creator)
              .orElseThrow(EntityNotFoundException::new);
      String finalPullName = pullName.toLowerCase();
      int points = pointsForTask.entrySet().stream()
              .filter(entry -> finalPullName.contains(entry.getKey()))
              .map(Map.Entry::getValue).findFirst().orElse(0);
      if (points == 0) {
        sendMessageWhichDescribesZeroPoints(stateEntity.getUserID(), pullName);
      }
      int newUserPoints = stateEntity.getPointByTask() + points;
      try {
        if (countService.getCountedCompletedTasks().get(creator).size()
                == numberPullsAbilityReview) {
          sendAbilityReviewMess(stateEntity.getUserID());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      stateEntity.setPointByTask(newUserPoints);
      stateMachineRepository.save(stateEntity);
    }
  }

  /**
   * This method send AbilityReview message to the user, if user has 3 labels "done".
   * @param id Slack User Id
   */
  public void sendAbilityReviewMess(String id) {
    slackService.sendBlocksMessage(slackService.getUserById(id), abilityReviewMessage);
  }

  /**
   * This method send AbilityReview message to the user, if user misnamed pull request.
   * @param id Slack User Id
   */
  public void sendMessageWhichDescribesZeroPoints(String id, String pullName) {
    String messageDescribesZero = zeroPointsMessage.replace("pull_name", pullName);
    slackService.sendPrivateMessage(slackService.getUserById(id), messageDescribesZero);
  }
}
