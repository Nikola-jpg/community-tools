package com.community.tools.service.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreedLicensePayload extends Payload {

  private String gitNick;

  /**
   * Constructor for class.
   *
   * @param id      - usersId
   * @param gitNick - nick in Git
   */
  public AgreedLicensePayload(Integer id, String gitNick) {
    super(id);
    this.gitNick = gitNick;
  }
}
