package com.community.tools.service.discord;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.community.tools.model.Messages;
import com.community.tools.service.MessagesToPlatform;
import com.community.tools.service.PublishWeekStatsService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("discord")
public class MessagesToDiscord extends MessagesToPlatform<MessageEmbed> {

  MessagesToDiscord() {
    //messages for bot
    getFirstTask = new EmbedBuilder()
        .addField("", Messages.CONGRATS_AVAILABLE_NICK, false)
        .addField("", Messages.GET_FIRST_TASK + " [TASK]("
            + Messages.LINK_FIRST_TASK + ") :link:", false)
        .build();
    addGitName = new EmbedBuilder()
        .addField("", Messages.ADD_GIT_NAME, false)
        .build();
    noOneCase = new EmbedBuilder()
        .addField("", Messages.NO_ONE_CASE, false)
        .build();
    notThatMessage = new EmbedBuilder()
        .addField("", Messages.NOT_THAT_MESSAGE, false)
        .build();
    abilityReviewMessage = new EmbedBuilder()
        .addField("", Messages.ABILITY_REVIEW_MESSAGE, false)
        .build();
    firstQuestion = new EmbedBuilder()
        .addField("", Messages.FIRST_QUESTION, false)
        .build();
    secondQuestion = new EmbedBuilder()
        .addField("", Messages.SECOND_QUESTION, false)
        .build();
    thirdQuestion = new EmbedBuilder()
        .addField("", Messages.THIRD_QUESTION, false)
        .build();
    messageAboutRules = new EmbedBuilder()
        .addField("", Messages.MESSAGE_ABOUT_RULES_1, false)
        .addField("", "[Rules](" + Messages.MESSAGE_ABOUT_RULES_2 + ") :link:", false)
        .addField("", Messages.MESSAGE_ABOUT_RULES_3, false)
        .addField("", Messages.MESSAGE_ABOUT_RULES_4, false)
        .build();

    errorWithAddingGitName = new EmbedBuilder()
        .addField("", Messages.ERROR_WITH_ADDING_GIT_NAME, false)
        .addField("",
            "[*Liliya Stepanovna*](https://discord.com/channels/@me/842774422792437781)", false)
        .setThumbnail(
            "https://cdn-0.emojis.wiki/emoji-pics/facebook/woman-technologist-facebook.png")
        .build();

    estimateTheTask = new EmbedBuilder()
        .setTitle(Messages.ESTIMATE_HEADER)
        .addField("", "*1*" + Messages.ESTIMATE_QUESTION_FIRST, false)
        .addField("", "*2*" + Messages.ESTIMATE_QUESTION_SECOND, false)
        .addField("", "*3*" + Messages.ESTIMATE_QUESTION_THIRD, false)
        .addField("", "*4*" + Messages.ESTIMATE_QUESTION_FOURTH, false)
        .addField("", "*5*" + Messages.ESTIMATE_QUESTION_FIFTH, false)
        .addField("", "`" + Messages.ESTIMATE_FOOTER + "` :point_down:", false)
        .build();

    //Information channels message
    messageAboutSeveralInfoChannel = new EmbedBuilder()
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

  @Override
  public MessageEmbed failedBuildMessage(String url, String task) {
    return new EmbedBuilder()
        .addField("", "Oops, your build at the task [" + task + "](" + url + ") is down!", false)
        .build();
  }

  @Override
  public MessageEmbed infoLinkMessage(String info, String url, String img) {
    return new EmbedBuilder()
        .setTitle(info, url)
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
              + authorsActivMessage, false);
        });
    return embedBuilder.build();
  }

  @Override
  public MessageEmbed nextTaskMessage(List<String> tasksList, int numberTask) {
    return new EmbedBuilder()
        .addField("", NEXT_TASK + tasksList.get(numberTask) + ") :link:", false)
        .build();
  }

  public static final String NEXT_TASK = "Here is your next [TASK]("
      + "https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/";
}
