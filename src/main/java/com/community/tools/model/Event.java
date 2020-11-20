package com.community.tools.model;

public enum Event {
  PULL_REQUEST_CREATED("Pull Request created"),
  PULL_REQUEST_CLOSED("Pull Request closed"),
  COMMIT("Commit"),
  COMMENT("Comment");

  private final String title;

  public String getTitle() {
    return title;
  }

  Event(String title) {
    this.title = title;
  }
}