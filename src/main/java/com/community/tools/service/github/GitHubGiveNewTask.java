package com.community.tools.service.github;

import com.community.tools.service.StateMachineService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@Service
public class GitHubGiveNewTask {
  @Value("${git.number.of.tasks}")
  private Integer numberOfTasks;
  @Autowired
  private StateMachinePersister<State, Event, String> persister;
  @Autowired
  private StateMachineService stateMachineService;

  /**
   * Give new Task to the trainee. Checks for the last task.
   * @param user GitHub login of trainee
   */
  public void giveNewTask(String user) {
    try {
      StateMachine<State, Event> machine = stateMachineService.restoreMachineByNick(user);
      machine.sendEvent(Event.GET_THE_NEW_TASK);
      if (machine.getExtendedState().getVariables().get("taskNumber") ==  numberOfTasks) {
        machine.sendEvent(Event.LAST_TASK);
      } else {
        machine.sendEvent(Event.CHANGE_TASK);
      }
      persister.persist(machine,stateMachineService.getIdByNick(user));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
