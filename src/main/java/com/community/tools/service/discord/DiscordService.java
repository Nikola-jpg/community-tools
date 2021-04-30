package com.community.tools.service.discord;

import com.community.tools.service.MessageService;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//@Primary
public class DiscordService implements MessageService {

  private final JDA jda;

  @Autowired
  private DiscordEventListener discordEventListener;

  @PostConstruct
  private void postConstruct() {
    jda.addEventListener(discordEventListener);
  }

  /**
   * Send private message with messageText to username.
   *
   * @param username    Discord login
   * @param messageText Text of message
   * @return timestamp of message
   */
  @Override
  public String sendPrivateMessage(String username, String messageText) {
    jda.getUserById(getIdByUsername(username)).openPrivateChannel().queue((channel) -> {
      channel.sendMessage(messageText).queue();
    });
    return "";
  }

  /**
   * Send block message with messageText to username.
   *
   * @param username    Discord login
   * @param message object of MessageEmbed
   * @return timestamp of message
   */
  @Override
  public <T> String sendBlocksMessage(String username, T message) {
    jda.getUserById(getIdByUsername(username)).openPrivateChannel().queue((channel) -> {
      channel.sendMessage((MessageEmbed) message).queue();
    });
    return "";
  }

  /**
   * Send attachment message with messageText to username.
   *
   * @param username    Discord login
   * @param message object of MessageEmbed
   * @return timestamp of message
   */
  @Override
  public <T> String sendAttachmentsMessage(String username, T message) {
    jda.getUserById(getIdByUsername(username)).openPrivateChannel().queue((channel) -> {
      channel.sendMessage((MessageEmbed) message).queue();
    });
    return "";
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
    jda.getTextChannelById(getIdByChannelName(channelName))
          .sendMessage(messageText).queue();
    return "";
  }

  /**
   * Send attachment message with blocks of Text to the channel.
   *
   * @param channelName Name of channel
   * @param message object of MessageEmbed
   * @return timestamp of message
   */
  @Override
  public <T> String sendBlockMessageToConversation(String channelName, T message) {
    jda.getTextChannelById(getIdByChannelName(channelName))
        .sendMessage((MessageEmbed) message).queue();
    return "";
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
    return jda.getUserById(id).getName();
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
