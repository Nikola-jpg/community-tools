package com.community.tools.service.discord;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.community.tools.service.MessageService;
import com.community.tools.service.PublishWeekStatsService;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
@Profile("discord")
public class DiscordService implements MessageService<MessageEmbed> {

  private JDA jda;

  @Autowired
  private DiscordEventListener discordEventListener;

  @Autowired
  public void setJda(JDA jda) {
    jda.addEventListener(discordEventListener);
    this.jda = jda;
  }

  /**
   * Send private message with messageText to username.
   *
   * @param username    Discord login
   * @param messageText Text of message
   */
  @Override
  public void sendPrivateMessage(String username, String messageText) {
    jda.getUserById(getIdByUsername(username)).openPrivateChannel().queue((channel) -> {
      channel.sendMessage(messageText).queue();
    });
  }

  /**
   * Send block message with messageText to username.
   *
   * @param username    Discord login
   * @param message object of MessageEmbed
   */
  @Override
  public <T> void sendBlocksMessage(String username, T message) {
    jda.getUserById(getIdByUsername(username)).openPrivateChannel().queue((channel) -> {
      channel.sendMessage((MessageEmbed) message).queue();
    });
  }

  /**
   * Send attachment message with messageText to username.
   *
   * @param username    Discord login
   * @param message object of MessageEmbed
   */
  @Override
  public <T> void sendAttachmentsMessage(String username, T message) {
    jda.getUserById(getIdByUsername(username)).openPrivateChannel().queue((channel) -> {
      channel.sendMessage((MessageEmbed) message).queue();
    });
  }

  /**
   * Send attachment message with messageText to channel.
   *
   * @param channelName Name of channel
   * @param messageText Text of message
   */
  @Override
  public void sendMessageToConversation(String channelName, String messageText) {
    jda.getTextChannelById(getIdByChannelName(channelName))
          .sendMessage(messageText).queue();
  }

  /**
   * Send attachment message with blocks of Text to the channel.
   *
   * @param channelName Name of channel
   * @param message object of MessageEmbed
   */
  @Override
  public <T> void sendBlockMessageToConversation(String channelName, T message) {
    MessageEmbed messageEmbed = (MessageEmbed) message;

    if (!(messageEmbed.getImage() == null)) {
      try {
        InputStream file = new URL(messageEmbed.getImage().getUrl()).openStream();
        jda.getTextChannelById(getIdByChannelName(channelName))
            .sendMessage(messageEmbed).addFile(file, "image.png").queue();
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }
    } else {
      jda.getTextChannelById(getIdByChannelName(channelName))
          .sendMessage(messageEmbed).queue();
    }
  }

  @Override
  public MessageEmbed nextTaskMessage(List<String> tasksList, int numberTask) {
    return new EmbedBuilder()
        .addField("", MessagesToDiscord.NEXT_TASK + tasksList.get(numberTask) + ") :link:", false)
        .build();
  }

  @Override
  public MessageEmbed ratingMessage(String url, String img) {
    return  new EmbedBuilder()
        .setTitle(":point_right: Рейтинг этой недели :point_left:", url)
        .setImage(img)

        .build();
  }

  @Override
  public MessageEmbed statisticMessage(List<EventData> events) {

    EmbedBuilder embedBuilder = new EmbedBuilder();

    Map<String, List<EventData>> sortedMapGroupByActors = new HashMap<>();
    events.stream().filter(ed -> !sortedMapGroupByActors.containsKey(ed.getActorLogin()))
        .forEach(ed -> sortedMapGroupByActors.put(ed.getActorLogin(), new ArrayList<>()));

    embedBuilder.addField("", "`Statistic:`", false);

    events.stream()
        .collect(Collectors.groupingBy(EventData::getType))
        .entrySet().stream()
        .sorted(Comparator
            .comparingInt((Entry<Event, List<EventData>> entry)
                -> entry.getValue().size()).reversed())
        .forEach(entry -> {
          entry.getValue().forEach(e -> sortedMapGroupByActors.get(e.getActorLogin()).add(e));

          embedBuilder.addField("", PublishWeekStatsService.getTypeTitleBold(entry.getKey())
              + PublishWeekStatsService.emojiGen(entry.getKey()) + ": "
              + entry.getValue().size(), false);
        });
    embedBuilder.addField("", "`Activity:`", false);

    sortedMapGroupByActors.entrySet().stream()
        .sorted(Comparator
            .comparingInt((Entry<String, List<EventData>> entry)
                -> entry.getValue().size()).reversed())
        .forEach(name -> {
          StringBuilder authorsActivMessage = new StringBuilder();
          name.getValue()
              .forEach(eventData -> {
                authorsActivMessage.append(PublishWeekStatsService.emojiGen(eventData.getType()));
              });
          embedBuilder.addField("", name.getKey() + ": "
              + authorsActivMessage,false);
        });
    return embedBuilder.build();
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
    throw new NotImplementedException("This functionality is not implemented in this release.");
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
