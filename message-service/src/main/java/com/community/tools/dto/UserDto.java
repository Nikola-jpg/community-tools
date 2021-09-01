package com.community.tools.dto;

import lombok.Data;
import net.dv8tion.jda.api.entities.User;

/** javadoc. */
@Data
public class UserDto {
  private String id;
  private String name;
  private String displayName;

  /**
   * hello.
   * @param anotherUser - discordUser.
   * @return
   */
  public static UserDto fromDiscord(User anotherUser) {
    UserDto user = new UserDto();

    user.setId(anotherUser.getId());
    user.setName(anotherUser.getName());

    return user;
  }

  /**
   * hello.
   * @param anotherUser - slackUser.
   * @return
   */
  public static UserDto fromSlack(com.github.seratch.jslack.api.model.User anotherUser) {
    UserDto user = new UserDto();

    user.setId(anotherUser.getId());
    user.setName(anotherUser.getRealName());
    user.setDisplayName(anotherUser.getProfile().getDisplayName());

    return user;
  }
}
