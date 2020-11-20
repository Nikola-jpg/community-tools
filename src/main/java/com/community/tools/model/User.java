package com.community.tools.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "state_entity")
public class User {
  @Id
  private String userID;
  private String gitName;
  private byte[] stateMachine;
  private int karma = 0;
  private Integer pointByTask = 0;
}
