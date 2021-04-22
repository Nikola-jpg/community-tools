package com.community.tools.util.statemachie;

public enum Event {
  AGREE_LICENSE, ADD_GIT_NAME, GET_THE_FIRST_TASK, GET_THE_NEW_TASK,
  CHANGE_TASK, LAST_TASK, LOGIN_CONFIRMATION, DID_NOT_PASS_VERIFICATION_GIT_LOGIN,
  QUESTION_FIRST, QUESTION_SECOND, QUESTION_THIRD, CHANNELS_INFORMATION;

  /**
   * Methods for getting next event.
   *
   * @return - next event
   */
  public Event getNext() {
    Event[] events = Event.values();
    int item = this.ordinal();
    if (item < events.length) {
      return item == events.length - 1
          ? events[0]
          : events[item + 1];
    } else {
      return null;
    }
  }
}
