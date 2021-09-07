package com.community.tools.discord;
/** MessageToDiscrod. */

import com.community.tools.dto.EventDataDto;
import com.community.tools.service.MessageConstructor;

import com.community.tools.util.MessageUtils;
import java.util.ArrayList;
import java.util.Arrays;
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
public class MessagesToDiscord implements MessageConstructor<MessageEmbed> {

  public static String[] INFO_CHANNEL_MESSAGES_CODE = {
    "",
    "<#834693025547943966>\n",
    "<#834692800586055710>\n",
    "<#834691593512550403>\n",
    "<#834692970048651285>\n",
    "<#834693307967602698>\n",
    ""
  };

  @Override
  public MessageEmbed createGetFirstTaskMessage(
      String availableNickMessage, String getFirstTaskMessage, String linkFirstTaskMessage) {
      EmbedBuilder builder = new EmbedBuilder();
        builder.addField("", availableNickMessage, false);
        builder.addField("", getFirstTaskMessage + " [TASK](" + linkFirstTaskMessage + ") :link",
                false);

    return builder.build();
  }

  @Override
  public MessageEmbed createAddGitNameMessage(String addGitNameMessage) {
    return new EmbedBuilder().addField("", addGitNameMessage, false).build();
  }

  @Override
  public MessageEmbed createNoOneCaseMessage(String noOneCaseMessage) {
    return new EmbedBuilder().addField("", noOneCaseMessage, false).build();
  }

  @Override
  public MessageEmbed createNotThatMessage(String noThatMessage) {
    return new EmbedBuilder().addField("", noThatMessage, false).build();
  }

  @Override
  public MessageEmbed createAbilityReviewMessage(String abilityReviewMessage) {
    return new EmbedBuilder().addField("", abilityReviewMessage, false).build();
  }

  @Override
  public MessageEmbed createFirstQuestion(String firstQuestion) {
    return new EmbedBuilder().addField("", firstQuestion, false).build();
  }

  @Override
  public MessageEmbed createSecondQuestion(String secondQuestion) {
    return new EmbedBuilder().addField("", secondQuestion, false).build();
  }

  @Override
  public MessageEmbed createThirdQuestion(String thirdQuestion) {
    return new EmbedBuilder().addField("", thirdQuestion, false).build();
  }

  @Override
  public MessageEmbed createMessageAboutRules(
      String firstRule, String secondRule, String thirdRule, String fourthRule) {
    return new EmbedBuilder()
        .addField("", firstRule, false)
        .addField("", "[Rules](" + secondRule + ") :link;", false)
        .addField("", thirdRule, false)
        .addField("", fourthRule, false)
        .build();
  }

  @Override
  public MessageEmbed createErrorWithAddingGitNameMessage(String errorMessage) {
    return new EmbedBuilder()
        .addField("", errorMessage, false)
        .addField(
            "", "[*Liliya Stepanovna*](https://discord.com/channels/@me/842774422792437781)", false)
        .setThumbnail(
            "https://cdn-0.emojis.wiki/emoji-pics/facebook/woman-technologist-facebook.png")
        .build();
  }

  /**javadoc.
   * @param header - header.
   * @param estimateQuestions - arrayOfQuestion.
   * @param footer - footer.
   * @return - builder.
   */
  public MessageEmbed createEstimateTheTaskMessage(
      String header, String[] estimateQuestions, String footer) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.setTitle(header);
    Arrays.stream(estimateQuestions).forEachOrdered(eq -> builder.addField("", eq, false));
    builder.addField("", footer, false);

    return builder.build();
  }

  @Override
  public MessageEmbed createMessageAboutSeveralInfoChannel(String[] infoChannelMessages) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.appendDescription(infoChannelMessages[0]);
    for (int i = 1; i < infoChannelMessages.length; i++) {
      builder.addField("", INFO_CHANNEL_MESSAGES_CODE[i] + infoChannelMessages[i], false);
    }
    return builder.build();
  }

  @Override
  public MessageEmbed failedBuildMessage(String url, String task) {
    return new EmbedBuilder()
        .addField("", "Oops, your build at the task [" + task + "](" + url + ") is down!", false)
        .build();
  }

  @Override
  public MessageEmbed infoLinkMessage(String info, String url, String img) {
    return new EmbedBuilder().setTitle(info, url).setImage(img).build();
  }

  @Override
  public MessageEmbed statisticMessage(List<EventDataDto> events) {
    EmbedBuilder embedBuilder = new EmbedBuilder();

    Map<String, List<EventDataDto>> sortedMapGroupByActors = new HashMap<>();
    events.stream()
        .filter(ed -> !sortedMapGroupByActors.containsKey(ed.getActorLogin()))
        .forEach(ed -> sortedMapGroupByActors.put(ed.getActorLogin(), new ArrayList<>()));
    embedBuilder.addField("", "`Statistic:`", false);

    events.stream().collect(Collectors.groupingBy(EventDataDto::getType)).entrySet().stream()
        .sorted(
            Comparator.comparingInt((Entry<Map<String, String>, List<EventDataDto>> entry) ->
                            entry.getValue()
                            .size())
                    .reversed())
        .forEach(
            entry -> {
              entry.getValue().forEach(e -> sortedMapGroupByActors.get(e.getActorLogin()).add(e));
              embedBuilder.addField(
                  "",
                  MessageUtils.getTypeTitleBold(
                          entry.getKey().keySet().stream().findFirst().toString())
                      + MessageUtils.emojiGen(
                          entry.getKey().keySet().stream().findFirst().toString())
                      + ": "
                      + entry.getValue().size(),
                  false);
            });
    embedBuilder.addField("", "`Activity:`", false);

    sortedMapGroupByActors.entrySet().stream()
        .sorted(
            Comparator.comparingInt((Entry<String, List<EventDataDto>> entry) ->
                            entry.getValue().size())
                    .reversed())
        .forEach(
            name -> {
              StringBuilder authorsActiveMessage = new StringBuilder();
              name.getValue()
                  .forEach(
                      eventData -> {
                        authorsActiveMessage.append(
                            MessageUtils.emojiGen(
                                eventData.getType().keySet().stream().findFirst().toString()));
                      });
              embedBuilder.addField("", name.getKey() + ": " + authorsActiveMessage, false);
            });
    return embedBuilder.build();
  }

  @Override
  public MessageEmbed nextTaskMessage(List<String> tasksList, int numberTask) {
    return new EmbedBuilder()
        .addField("", NEXT_TASK + tasksList.get(numberTask) + ") :link:", false)
        .build();
  }

  public static final String NEXT_TASK =
      "Here is your next [TASK]("
          + "https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/";
}
