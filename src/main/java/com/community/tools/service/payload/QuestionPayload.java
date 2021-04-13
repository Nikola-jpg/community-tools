package com.community.tools.service.payload;

import com.github.seratch.jslack.api.model.event.MessageEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionPayload extends Payload {

  public QuestionPayload(Integer id, MessageEvent messageEvent) {
    super(id, messageEvent);
  }
}
