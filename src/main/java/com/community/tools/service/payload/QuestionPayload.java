package com.community.tools.service.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionPayload extends Payload {

  private String eventText;

  private String user;

  public QuestionPayload(Integer id, String eventText, String user) {
    super(id);
    this.eventText = eventText;
    this.user = user;
  }
}
