package com.community.tools.service.payload;

import com.github.seratch.jslack.api.model.event.MessageEvent;

public class AddedGitPayload extends Payload {

  public AddedGitPayload(Integer id, MessageEvent messageEvent) {
    super(id, messageEvent);
  }
}
