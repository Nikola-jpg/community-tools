package com.community.tools.service;

import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@Service
public class GiveNewTaskService {

  @Value("${git.number.of.tasks}")
  private Integer numberOfTasks;
  @Autowired
  private StateMachinePersister<State, Event, String> persister;
  @Autowired
  private StateMachineService stateMachineService;

  /**
   * Give new Task to the trainee. Checks for the last task.
   *
   * @param userId - id user
   */
  public void giveNewTask(String userId) {
    try {
      StateMachine<State, Event> machine = stateMachineService.restoreMachine(userId);
      machine.sendEvent(Event.GET_THE_NEW_TASK);
      if (machine.getExtendedState().getVariables().get("taskNumber") == numberOfTasks) {
        machine.sendEvent(Event.LAST_TASK);
      } else {
        machine.sendEvent(Event.CHANGE_TASK);
      }
      persister.persist(machine, userId);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
