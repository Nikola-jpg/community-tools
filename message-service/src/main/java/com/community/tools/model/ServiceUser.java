package com.community.tools.model;

import lombok.Data;
import net.dv8tion.jda.api.entities.User;

@Data
public class ServiceUser {
  private String id;
  private String name;


  /**
   * Convert an DiscordUser object into an UserDto object.
   *
   * @param discordUser - DiscordUser object.
   * @return a new UserDto object.
   */
  public static ServiceUser fromDiscord(User discordUser) {
    ServiceUser user = new ServiceUser();

    user.setId(discordUser.getId());
    user.setName(discordUser.getName());

    return user;
  }

  /**
   * Convert an SlackUser object into an UserDto object.
   * @param slackUser - SlackUser object.
   * @return a new UserDto object.
   */
  public static ServiceUser fromSlack(com.github.seratch.jslack.api.model.User slackUser) {
    ServiceUser user = new ServiceUser();

    user.setId(slackUser.getId());
    user.setName(slackUser.getRealName());

    return user;
  }
}
