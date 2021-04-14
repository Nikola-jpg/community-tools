package com.community.tools.service.payload;

import com.github.seratch.jslack.api.model.event.MessageEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payload {

  protected Integer id;

  public Payload(Integer id) {
    this.id = id;
  }
}
