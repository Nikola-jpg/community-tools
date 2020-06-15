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
    private String git_name;

    public StateEntity() {
    }

    public StateEntity(String userID, byte[] stateMachine) {
        this.stateMachine = stateMachine;
        this.userID = userID;
    }
    public StateEntity(String userID, String git_name) {
        this.git_name = git_name;
        this.userID = userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setGit_name(String git_name) {
        this.git_name = git_name;
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

    public String getGit_name() {
        return git_name;
    }
}
