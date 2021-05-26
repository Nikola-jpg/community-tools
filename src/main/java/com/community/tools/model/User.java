package com.community.tools.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "state_entity")
public class User {
  @Id
  private String userID;
  private String gitName;
  private byte[] stateMachine;
  private Integer karma = 0;
  private Integer pointByTask = 0;
  private String firstAnswerAboutRules;
  private String secondAnswerAboutRules;
  private String thirdAnswerAboutRules;

  private String platformName;

  private Integer completedTasks;

  /**
   * This method summ karma and pointsBy task. If fields null, return 0.
    * @return Total points
   */
  public Integer getTotalPoints() {
    if (this.karma == null & this.pointByTask == null) {
      return 0;
    } else {
      if (this.karma == null) {
        return this.pointByTask;
      } else {
        if (this.pointByTask == null) {
          return this.karma;
        } else {
          return this.karma + this.pointByTask;
        }
      }
    }
  }
}
