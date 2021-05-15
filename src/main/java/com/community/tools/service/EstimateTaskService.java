package com.community.tools.service;

import com.community.tools.model.Task;
import com.community.tools.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstimateTaskService {

  @Autowired
  TaskRepository taskRepository;

  /**
   * Save estimate task in database.
   *
   * @param userId     - id users
   * @param taskNumber - taskNumber for saving
   * @param estimateId - id estimate
   */
  public void saveEstimateTask(String userId, Integer taskNumber, String estimateId) {
    Task task = taskRepository.findByUserIdAndTaskNumber(userId, taskNumber);
    if (task == null) {
      task = Task.builder()
          .userId(userId)
          .taskNumber(taskNumber)
          .estimateId(Integer.parseInt(estimateId))
          .build();
    } else {
      task.setEstimateId(Integer.parseInt(estimateId));
    }
    taskRepository.save(task);
  }
}
