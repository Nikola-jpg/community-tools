package com.community.tools.service.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscordService {

  @Autowired
  private JDA jda;

  public String sendPrivateMessage(String username, String messageText) {
    try {
      jda.awaitReady();
      jda.getUserById(getIdUserByUsername(username)).openPrivateChannel().queue((channel) ->
      {
        channel.sendMessageFormat(messageText).queue();
      });

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }

  public String sendBlocksMessage(String username, String messageText) {
    try {
      EmbedBuilder embedBuilder = new EmbedBuilder();

      embedBuilder.setDescription(messageText);
      jda.awaitReady();
      jda.getUserById(getIdUserByUsername(username)).openPrivateChannel().queue((channel) ->
      {
        channel.sendMessage(embedBuilder.build()).queue();
      });

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }

  public String sendAttachmentsMessage(String username, String messageText) {
    try {
      EmbedBuilder embedBuilder = new EmbedBuilder();
      embedBuilder.setDescription(messageText);
      jda.awaitReady();
      jda.getUserById(getIdUserByUsername(username)).openPrivateChannel().queue((channel) ->
      {
        channel.sendMessage(embedBuilder.build()).queue();
      });

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }


  public String sendMessageToConversation(String channelName, String messageText) {
    try {
      jda.awaitReady();
      jda.getTextChannelById(getIdChannelByChannelName(channelName))
          .sendMessage(messageText).queue();

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }


  public String sendBlockMessageToConversation(String channelName, String messageText) {
    try {
      EmbedBuilder embedBuilder = new EmbedBuilder();
      embedBuilder.setDescription(messageText);
      jda.awaitReady();
      jda.getTextChannelById(getIdChannelByChannelName(channelName))
          .sendMessage(embedBuilder.build()).queue();

      return "";
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }





  public String getChannelById(String channelId) {
    TextChannel textChannel = jda.getTextChannelById(channelId);
    return textChannel.getName();
  }

  public String getIdChannelByChannelName(String channelName) {
    TextChannel channel = jda.getTextChannels().stream()
        .filter(textChannel -> textChannel.getName().equals(channelName))
        .findFirst().get();
    String channelId = channel.getId();
    return channelId;
  }

  public String getUserById(String userId) {
    User user =  jda.getUserById(userId);
    return user.getName();
  }

  public String getIdUserByUsername(String username) {
    User user = jda.getUsers().stream().filter(u -> u.getName().equals(username)).findFirst().get();
    return user.getId();
  }

}
