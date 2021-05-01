package com.community.tools.service;

import com.community.tools.model.User;
import com.community.tools.service.discord.MessagesToDiscord;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.service.slack.MessagesToSlack;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;

import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
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
  StateMachineService stateMachineService;

  @Autowired
  MentorsRepository mentorsRepository;

  @Autowired
  StateMachineRepository stateMachineRepository;

  final int numberPullsAbilityReview = 3;

  @Autowired
  private BlockService blockService;

  @Autowired
  private Map<String, MessageService> messageServiceMap;

  @Value("${currentMessageService}")
  private String currentMessageService;

  /**
   * Selected current message service.
   * @return current message service
   */
  public MessageService getMessageService() {
    return messageServiceMap.get(currentMessageService);
  }

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

      StateMachine<State, Event> machine = stateMachineService
              .restoreMachineByNick(creator);
      int taskDone = (int) machine.getExtendedState()
              .getVariables().getOrDefault("taskDone", 0);
      machine.getExtendedState().getVariables()
              .put("taskDone", ++taskDone);
      stateMachineService.persistMachine(machine, stateEntity.getUserID());

      String finalPullName = pullName.toLowerCase();
      int points = pointsForTask.entrySet().stream()
              .filter(entry -> finalPullName.contains(entry.getKey()))
              .map(Map.Entry::getValue).findFirst().orElse(0);
      if (points == 0) {
        sendMessageWhichDescribesZeroPoints(stateEntity.getUserID(), pullName);
      }
      int newUserPoints = stateEntity.getPointByTask() + points;
      if (taskDone == numberPullsAbilityReview) {
        sendAbilityReviewMess(stateEntity.getUserID());
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
    getMessageService().sendBlocksMessage(getMessageService().getUserById(id),
        blockService.createBlockMessage(MessagesToSlack.ABILITY_REVIEW_MESSAGE,
            MessagesToDiscord.ABILITY_REVIEW_MESSAGE));
  }

  /**
   * This method send AbilityReview message to the user, if user misnamed pull request.
   * @param id Slack User Id
   */
  public void sendMessageWhichDescribesZeroPoints(String id, String pullName) {
    String messageDescribesZero = zeroPointsMessage.replace("pull_name", pullName);
    getMessageService().sendPrivateMessage(getMessageService().getUserById(id),
        messageDescribesZero);
  }
}
