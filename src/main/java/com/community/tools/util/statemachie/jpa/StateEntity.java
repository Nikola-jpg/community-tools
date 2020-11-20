package com.community.tools.util.statemachie.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "state_entity")
public class StateEntity {
  @Id
  private String userID;
  private String gitName;
  private byte[] stateMachine;
  private Integer pointByTask = 0;
  private Integer karma = 0;

  public StateEntity() {
  }

  public StateEntity(String userID, byte[] stateMachine) {
    this.stateMachine = stateMachine;
    this.userID = userID;
  }

  public StateEntity(String userID, String gitName) {
    this.gitName = gitName;
    this.userID = userID;
  }

  public Integer getTotalPoints() {
    return this.karma + this.pointByTask;
  }

}
