package com.community.tools.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "state_entity")
public class User {
  @Id
  private String userID;
  private String gitName;
  private byte[] stateMachine;
  private String firstAnswerAboutRules;
  private String secondAnswerAboutRules;
  private String thirdAnswerAboutRules;

}
