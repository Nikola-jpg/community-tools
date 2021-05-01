package com.community.tools.service;

import com.community.tools.model.Task;
import com.community.tools.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstimateTaskService {

  @Autowired
  TaskRepository taskRepository;

  public void saveEstimate(String userId, Integer taskNumber, String estimateId){
    Task task = taskRepository.findByUserIdAndTaskNumber(userId, taskNumber);
    if(task == null) {
      Task newTask = Task.builder()
          .userId(userId)
          .taskNumber(taskNumber)
          .estimateId(Integer.parseInt(estimateId))
          .build();
      taskRepository.save(newTask);
    } else {
      task.setEstimateId(Integer.parseInt(estimateId));
    }
  }

}
