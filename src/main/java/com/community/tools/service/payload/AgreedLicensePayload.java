package com.community.tools.service.payload;

import com.github.seratch.jslack.api.model.event.MessageEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreedLicensePayload extends Payload {

  private String gitNick;

  public AgreedLicensePayload(Integer id, MessageEvent messageEvent, String gitNick) {
    super(id, messageEvent);
    this.gitNick = gitNick;
  }
}
