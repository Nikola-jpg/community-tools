package com.community.tools.util.statemachie.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "state_entity")
public class StateEntity {
    @Id
    private String userID;
    private String gitName;
    private byte[] stateMachine;

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
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setGitName(String gitName) {
        this.gitName = gitName;
    }

    public void setStateMachine(byte[] stateMachine) {
        this.stateMachine = stateMachine;
    }

    public String getUserID() {
        return userID;
    }

    public byte[] getStateMachine() {
        return stateMachine;
    }

    public String getGitName() {
        return gitName;
    }
}
