package com.community.tools.dto;

import lombok.Data;
import net.dv8tion.jda.api.entities.User;

@Data
public class UserDto {
  private String id;
  private String name;
  private String displayName;
  private com.github.seratch.jslack.api.model.User.Profile profile;

  /**
   * Convert an DiscordUser object into an UserDto object.
   *
   * @param discordUser - DiscordUser object.
   * @return a new UserDto object.
   */
  public static UserDto fromDiscord(User discordUser) {
    UserDto user = new UserDto();

    user.setId(discordUser.getId());
    user.setName(discordUser.getName());

    return user;
  }

  /**
   * Convert an SlackUser object into an UserDto object.
   *
   * @param slackUser - SlackUser object.
   * @return a new UserDto object.
   */
  public static UserDto fromSlack(com.github.seratch.jslack.api.model.User slackUser) {
    UserDto user = new UserDto();

    user.setId(slackUser.getId());
    user.setName(slackUser.getRealName());
    user.setDisplayName(slackUser.getProfile().getDisplayName());
    user.setProfile(slackUser.getProfile());

    return user;
  }
}
