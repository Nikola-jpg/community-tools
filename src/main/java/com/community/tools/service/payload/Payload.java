package com.community.tools.service.payload;

import com.github.seratch.jslack.api.model.event.MessageEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payload {

  protected Integer id;

  protected MessageEvent messageEvent;

  public Payload(Integer id, MessageEvent messageEvent) {
    this.id = id;
    this.messageEvent = messageEvent;
  }
}
