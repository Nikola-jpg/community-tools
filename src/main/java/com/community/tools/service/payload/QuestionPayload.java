package com.community.tools.service.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionPayload extends Payload {

  private String eventText;

  public QuestionPayload(Integer id, String eventText) {
    super(id);
    this.eventText = eventText;
  }
}
