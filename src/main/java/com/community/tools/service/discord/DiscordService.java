package com.community.tools.service.discord;

import com.community.tools.service.MessageService;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscordService implements MessageService {

  @Autowired
  private JDA jda;

  /**
   * Send private message with messageText to username.
   *
   * @param username    Discord login
   * @param messageText Text of message
   * @return timestamp of message
   */
  @Override
  public String sendPrivateMessage(String username, String messageText) {
    try {
      jda.awaitReady();
      jda.getUserById(getIdByUsername(username)).openPrivateChannel().queue((channel) -> {
        channel.sendMessage(messageText).queue();
      }
      );

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Send block message with messageText to username.
   *
   * @param username    Discord login
   * @param messageText Text of message
   * @return timestamp of message
   */
  @Override
  public String sendBlocksMessage(String username, String messageText) {
    try {
      MessageBuilder messageBuilder = new MessageBuilder();
      messageBuilder.appendCodeBlock(messageText, "json");

      EmbedBuilder embedBuilder = new EmbedBuilder();
      jda.awaitReady();
      jda.getUserById(getIdByUsername(username)).openPrivateChannel().queue((channel) -> {
        channel.sendMessage(messageBuilder.build()).queue();
      });

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Send block message with messageText to username.
   *
   * @param username    Discord login
   * @param messageText Text of message
   * @return timestamp of message
   */
  public String sendBlocksMessageDiscord(String username, String messageText) {
    try {
      MessageBuilder messageBuilder = new MessageBuilder();
      messageBuilder.appendCodeBlock(messageText, "json");

      EmbedBuilder embedBuilder = new EmbedBuilder();
      embedBuilder.setAuthor(username)
          .addField("Description", messageText, true)
          .setThumbnail("https://s3-media3.fl.yelpcdn.com/bphoto/c7ed05m9lC2EmA3Aruue7A/o.jpg");

      jda.awaitReady();
      jda.getUserById(getIdByUsername(username)).openPrivateChannel().queue((channel) -> {
        channel.sendMessage(embedBuilder.build()).queue();
      });

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }


  /**
   * Send attachment message with messageText to username.
   *
   * @param username    Discord login
   * @param messageText Text of message
   * @return timestamp of message
   */
  @Override
  public String sendAttachmentsMessage(String username, String messageText) {
    try {
      EmbedBuilder embedBuilder = new EmbedBuilder();
      embedBuilder.setDescription(messageText);
      jda.awaitReady();
      jda.getUserById(getIdByUsername(username)).openPrivateChannel().queue((channel) -> {
        channel.sendMessage(embedBuilder.build()).queue();
      });

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Send attachment message with messageText to channel.
   *
   * @param channelName Name of channel
   * @param messageText Text of message
   * @return timestamp of message
   */
  @Override
  public String sendMessageToConversation(String channelName, String messageText) {
    try {
      jda.awaitReady();
      jda.getTextChannelById(getIdByChannelName(channelName))
          .sendMessage(messageText).queue();

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Send attachment message with blocks of Text to the channel.
   *
   * @param channelName Name of channel
   * @param messageText Blocks of message
   * @return timestamp of message
   */
  @Override
  public String sendBlockMessageToConversation(String channelName, String messageText) {
    try {
      EmbedBuilder embedBuilder = new EmbedBuilder();
      embedBuilder.setDescription(messageText);
      jda.awaitReady();
      jda.getTextChannelById(getIdByChannelName(channelName))
          .sendMessage(embedBuilder.build()).queue();

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public String sendMessageToChat(String channelName, String messageText) {
    return null;
  }

  /**
   * Get channel by Discord`s channelName.
   *
   * @param channelName Discord`s channelName
   * @return channelName of Channel
   */
  @Override
  public String getIdByChannelName(String channelName) {
    TextChannel channel = jda.getTextChannels().stream()
        .filter(textChannel -> textChannel.getName().equals(channelName))
        .findFirst().get();
    String channelId = channel.getId();
    return channelId;
  }

  @Override
  public void sendAnnouncement(String message) {

  }


  /**
   * Get channel by Discord`s id.
   *
   * @param id Discord`s id
   * @return channelName of Channel
   */
  public String getChannelById(String id) {
    TextChannel textChannel = jda.getTextChannelById(id);
    return textChannel.getName();
  }

  @Override
  public String getUserById(String id) {
    return null;
  }

  /**
   * Get user by Discord`s id.
   *
   * @param id Slack`s id
   * @return realName of User
   */
  @Override
  public String getIdByUser(String id) {
    User user =  jda.getUserById(id);
    return user.getName();

  }

  /**
   * Get user by Discord`s username.
   *
   * @param username Discord`s username
   * @return realName of User
   */
  @Override
  public String getIdByUsername(String username) {
    User user = jda.getUsers().stream().filter(u -> u.getName().equals(username)).findFirst().get();
    return user.getId();
  }


  /**
   * Get all Discord`s user.
   *
   * @return Set of users.
   */
  @Override
  public Set<User> getAllUsers() {
    Set<User> users = jda.getUsers().stream().collect(Collectors.toSet());

    return users;
  }
}
