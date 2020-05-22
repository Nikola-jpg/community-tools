package com.community.tools.util.statemachie;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.transition.Transition;


public class StateMachineApplicationListeer implements
    org.springframework.statemachine.listener.StateMachineListener<State, Event> {

  @Override
  public void stateChanged(org.springframework.statemachine.state.State from,
      org.springframework.statemachine.state.State to) {
    if (from.getId() != null) {
      System.out.println("Переход из статуса " + from.getId() + " в статус " + to.getId());
    }
  }

  @Override
  public void stateEntered(org.springframework.statemachine.state.State state) {

  }

  @Override
  public void stateExited(org.springframework.statemachine.state.State state) {

  }

  @Override
  public void eventNotAccepted(Message<Event> message) {
    System.out.println("ивент не принят " + message);
  }

  @Override
  public void transition(Transition<State, Event> transition) {

  }

  @Override
  public void transitionStarted(Transition<State, Event> transition) {

  }

  @Override
  public void transitionEnded(Transition<State, Event> transition) {

  }

  @Override
  public void stateMachineStarted(StateMachine<State, Event> stateMachine) {
    System.out.println("State machine started");
  }

  @Override
  public void stateMachineStopped(StateMachine<State, Event> stateMachine) {

  }

  @Override
  public void stateMachineError(StateMachine<State, Event> stateMachine,
      Exception e) {

  }

  @Override
  public void extendedStateChanged(Object o, Object o1) {

  }

  @Override
  public void stateContext(StateContext<State, Event> stateContext) {

  }
}
