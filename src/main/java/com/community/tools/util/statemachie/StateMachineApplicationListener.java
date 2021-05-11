package com.community.tools.util.statemachie;

import java.util.logging.Logger;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;

public class StateMachineApplicationListener extends StateMachineListenerAdapter<State, Event> {

  private static Logger logger = Logger.getLogger(StateMachineApplicationListener.class.getName());


  @Override
  public void eventNotAccepted(Message<Event> message) {
    logger.info("event not accepted " + message);
  }

  @Override
  public void stateMachineStarted(StateMachine<State, Event> stateMachine) {
    logger.info("State machine started");
  }

}
