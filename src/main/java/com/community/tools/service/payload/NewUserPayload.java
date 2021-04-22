package com.community.tools.service.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewUserPayload extends Payload {

  private Integer taskNumber;

  private String mentor;

  /**
   * Constructor for class.
   *
   * @param id         - usersId
   * @param taskNumber - variable for working Action
   * @param mentor     - variable for Action
   */
  public NewUserPayload(String id, Integer taskNumber, String mentor) {
    super(id);
    this.taskNumber = taskNumber;
    this.mentor = mentor;
  }
}
