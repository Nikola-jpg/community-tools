package com.community.tools.service.discord;

import com.community.tools.model.Messages;
import com.community.tools.service.MessagesToPlatform;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("discord")
public class MessagesToDiscord extends MessagesToPlatform<MessageEmbed> {

  MessagesToDiscord() {
    //messages for bot
    GET_FIRST_TASK = new EmbedBuilder()
        .addField("", Messages.CONGRATS_AVAILABLE_NICK, false)
        .addField("", Messages.GET_FIRST_TASK + " [TASK]("
            + Messages.LINK_FIRST_TASK + ") :link:", false)
        .build();
    ADD_GIT_NAME = new EmbedBuilder()
        .addField("", Messages.ADD_GIT_NAME, false)
        .build();
    NO_ONE_CASE = new EmbedBuilder()
        .addField("", Messages.NO_ONE_CASE, false)
        .build();
    NOT_THAT_MESSAGE = new EmbedBuilder()
        .addField("", Messages.NOT_THAT_MESSAGE, false)
        .build();
    ABILITY_REVIEW_MESSAGE = new EmbedBuilder()
        .addField("", Messages.ABILITY_REVIEW_MESSAGE, false)
        .build();
    FIRST_QUESTION = new EmbedBuilder()
        .addField("", Messages.FIRST_QUESTION, false)
        .build();
    SECOND_QUESTION = new EmbedBuilder()
        .addField("", Messages.SECOND_QUESTION, false)
        .build();
    THIRD_QUESTION = new EmbedBuilder()
        .addField("", Messages.THIRD_QUESTION, false)
        .build();
    MESSAGE_ABOUT_RULES = new EmbedBuilder()
        .addField("", Messages.MESSAGE_ABOUT_RULES_1, false)
        .addField("", "[Rules](" + Messages.MESSAGE_ABOUT_RULES_2 + ") :link:", false)
        .addField("", Messages.MESSAGE_ABOUT_RULES_3, false)
        .addField("", Messages.MESSAGE_ABOUT_RULES_4, false)
        .build();

    ERROR_WITH_ADDING_GIT_NAME = new EmbedBuilder()
        .addField("", Messages.ERROR_WITH_ADDING_GIT_NAME, false)
        .addField("",
            "[*Liliya Stepanovna*](https://discord.com/channels/@me/842774422792437781)", false)
        .setThumbnail("https://cdn-0.emojis.wiki/emoji-pics/facebook/woman-technologist-facebook.png")
        .build();

    ESTIMATE_THE_TASK = new  EmbedBuilder()
        .setTitle(Messages.ESTIMATE_HEADER)
        .addField("", "*1*" + Messages.ESTIMATE_QUESTION_FIRST, false)
        .addField("", "*2*" + Messages.ESTIMATE_QUESTION_SECOND, false)
        .addField("", "*3*" + Messages.ESTIMATE_QUESTION_THIRD, false)
        .addField("", "*4*" + Messages.ESTIMATE_QUESTION_FOURTH, false)
        .addField("", "*5*" + Messages.ESTIMATE_QUESTION_FIFTH, false)
        .addField("", "`" + Messages.ESTIMATE_FOOTER + "` :point_down:",false)
        .build();

    //Information channels message
    MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL = new EmbedBuilder()
        .appendDescription(Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_1)
        .addField("",
            "<#834693025547943966>\n"
                + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_2, false)
        .addField("",
            "<#834692800586055710>\n"
                + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_3, false)
        .addField("",
            "<#834691593512550403>\n"
                + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_4, false)
        .addField("",
            "<#834692970048651285>\n"
                + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_5, false)
        .addField("",
            "<#834693307967602698>\n"
                + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_6, false)
        .addField("", Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_7, false)
        .build();
  }
}
