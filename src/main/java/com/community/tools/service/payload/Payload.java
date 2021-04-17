package com.community.tools.service.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Payload {

  protected Integer id;

  public Payload(Integer id) {
    this.id = id;
  }
}
