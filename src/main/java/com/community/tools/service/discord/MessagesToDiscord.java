package com.community.tools.service.discord;

import com.community.tools.model.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class MessagesToDiscord {

  //messages for bot
  public static final MessageEmbed GET_FIRST_TASK = new EmbedBuilder()
      .addField("", Messages.CONGRATS_AVAILABLE_NICK, false)
      .addField("", Messages.GET_FIRST_TASK + " [TASK]("
          + Messages.LINK_FIRST_TASK + ") :link:", false)
      .build();
  public static final MessageEmbed ADD_GIT_NAME = new EmbedBuilder()
      .addField("", Messages.ADD_GIT_NAME, false)
      .build();
  public static final MessageEmbed NO_ONE_CASE = new EmbedBuilder()
      .addField("", Messages.NO_ONE_CASE, false)
      .build();
  public static final MessageEmbed NOT_THAT_MESSAGE = new EmbedBuilder()
      .addField("", Messages.NOT_THAT_MESSAGE, false)
      .build();
  public static final MessageEmbed ABILITY_REVIEW_MESSAGE = new EmbedBuilder()
      .addField("", Messages.ABILITY_REVIEW_MESSAGE, false)
      .build();
  public static final MessageEmbed FIRST_QUESTION = new EmbedBuilder()
      .addField("", Messages.FIRST_QUESTION, false)
      .build();
  public static final MessageEmbed SECOND_QUESTION = new EmbedBuilder()
      .addField("", Messages.SECOND_QUESTION, false)
      .build();
  public static final MessageEmbed THIRD_QUESTION = new EmbedBuilder()
      .addField("", Messages.THIRD_QUESTION, false)
      .build();
  public static final MessageEmbed MESSAGE_ABOUT_RULES = new EmbedBuilder()
      .addField("", Messages.MESSAGE_ABOUT_RULES_1, false)
      .addField("", "[Rules](" + Messages.MESSAGE_ABOUT_RULES_2 + ") :link:", false)
      .addField("", Messages.MESSAGE_ABOUT_RULES_3, false)
      .addField("", Messages.MESSAGE_ABOUT_RULES_4, false)
      .build();

  public static final MessageEmbed ERROR_WITH_ADDING_GIT_NAME = new EmbedBuilder()
      .addField("", Messages.ERROR_WITH_ADDING_GIT_NAME, false)
      .build();

  //Information channels message
  public static final MessageEmbed MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL = new EmbedBuilder()
      .appendDescription(Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_1)
      .addField("", Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_2, false)
      .addField("", Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_3, false)
      .addField("", Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_4, false)
      .addField("", Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_5, false)
      .addField("", Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_6, false)
      .addField("", Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_7, false)
      .build();

}
