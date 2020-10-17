package com.community.tools.service;

import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;



public class PointsTaskService {

  @Value("${abilityReviewMessage}")
  private String abilityReviewMessage;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @Value("${tasksForUsers}")
  private String[] pointsForTask;

  @Autowired
  private SlackService slackService;

  @Autowired
  private CountingCompletedTasksService countService;

  @Autowired
  MentorsRepository mentorsRepository;

  @Autowired
  StateMachineRepository stateMachineRepository;



  /**
   * This method adds points to the trainee, when menntor labeled pull as "done".
   * @param mentor GitNick of person, who add label "done" to  pull request
   * @param creator GitNick of person, who pull request
   * @param pullName Pull request title
   */
  @SneakyThrows
  public void addPointForCompletedTask(String mentor, String creator, String pullName) {
    if (mentorsRepository.findByGitNick(mentor).isPresent()) {
      StateEntity stateEntity = stateMachineRepository.findByGitName(creator)
              .orElseThrow(EntityNotFoundException::new);

      int points = checkPoints(pullName);
      int newUserPoints = stateEntity.getPointByTask() + points;
      if (countService.getCountedCompletedTasks().get(creator).size() == 3) {
        sendAbilityReviewMess(stateEntity.getUserID());
      }

      stateEntity.setPointByTask(newUserPoints);
      stateMachineRepository.save(stateEntity);
    }
  }

  /**
   * This method compares the name of the pull request with the name of the task and assigns points.
   * @param pullName Title of pull request
   * @return Number of points for task. If pull has wrong name, returns 0
   */
  public int checkPoints(String pullName) {
    List<String> tasksList = Arrays.stream(tasksForUsers)
            .map(String::trim).collect(Collectors.toList());
    pullName = pullName.toLowerCase();
    int number = tasksList.stream().filter(pullName::contains)
            .map(tasksList::indexOf).findFirst().orElse(-1);

    if (number == -1) {
      return 0;
    } else {
      return Integer.parseInt(pointsForTask[number]);
    }

  }

  /**
   * This method send AbilityReview message to the user, if user has 3 labels "done".
   * @param id Slack User Id
   */
  public void sendAbilityReviewMess(String id) {
    try {
      slackService.sendBlocksMessage(slackService.getUserById(id), abilityReviewMessage);
    } catch (JsonParseException e) {
      e.getMessage();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
