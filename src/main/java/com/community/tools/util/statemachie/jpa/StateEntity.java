package com.community.tools.util.statemachie.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "StateEntity")
public class StateEntity {
  @Id
  private String userID;
  private byte[] stateMachine;

  public StateEntity() {
  }

  public StateEntity(String userID, byte[] stateMachine) {
    this.stateMachine = stateMachine;
    this.userID = userID;
  }
  public String getUserID() {
    return userID;
  }

  public byte[] getStateMachine() {
    return stateMachine;
  }
}
