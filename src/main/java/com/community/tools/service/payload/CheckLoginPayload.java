package com.community.tools.service.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckLoginPayload extends Payload {

  private String gitNick;

  public CheckLoginPayload(String id, String gitNick) {
    super(id);
    this.gitNick = gitNick;
  }
}
