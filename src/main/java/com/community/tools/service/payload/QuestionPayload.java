package com.community.tools.service.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionPayload extends Payload {

  private String answer;

  private String user;

  /**
   * Constructor for class.
   *
   * @param id     - usersId
   * @param answer - text answer
   * @param user   - users id for adding answer
   */
  public QuestionPayload(Integer id, String answer, String user) {
    super(id);
    this.answer = answer;
    this.user = user;
  }
}
