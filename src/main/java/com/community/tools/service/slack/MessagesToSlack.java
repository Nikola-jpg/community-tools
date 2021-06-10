package com.community.tools.service.slack;

import com.community.tools.model.Messages;
import com.community.tools.service.MessagesToPlatform;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("slack")
public class MessagesToSlack extends MessagesToPlatform<String> {

  MessagesToSlack() {

    //messages for bot
    getFirstTask = "[{\"type\": \"section\",\"text\": {"
        + "\"type\":\"mrkdwn\",\"text\": \"" + Messages.CONGRATS_AVAILABLE_NICK + "\n\n"
        + Messages.GET_FIRST_TASK + " <"
        + Messages.LINK_FIRST_TASK + "|TASK> :link:\"}}]";
    addGitName = "[{\"type\": \"section\",\"text\": {"
        + "\"type\":\"mrkdwn\",\"text\": \"" + Messages.ADD_GIT_NAME + "\"}}]";
    noOneCase = "[{\"type\": \"divider\"},{\"type\": \"section\","
        + "\"text\": {\"type\": \"mrkdwn\",\"text\": \"" + Messages.NO_ONE_CASE + "\"},"
        + "\"accessory\": {\"type\": \"button\",\"text\": {\"type\": \"plain_text\","
        + "\"text\": \"Button\",\"emoji\": true},\"value\": \"Button\"}}]";
    notThatMessage = "[{\"type\": \"section\",\"text\": {"
        + "\"type\": \"mrkdwn\",\"text\": \"" + Messages.NOT_THAT_MESSAGE + "\"}}]";
    abilityReviewMessage = "[{\"type\": \"context\","
        + " \"elements\": [{\"type\": \"mrkdwn\",\"text\": \""
        + Messages.ABILITY_REVIEW_MESSAGE + "\"}]}]";
    firstQuestion = "[ {\"type\": \"context\", \"elements\": [{"
        + " \"type\": \"mrkdwn\", \"text\": \"```" + Messages.FIRST_QUESTION + "```\" } ] } ]";
    secondQuestion = "[ {\"type\": \"context\", \"elements\": [{"
        + " \"type\": \"mrkdwn\", \"text\": \"```" + Messages.SECOND_QUESTION + "```\" } ] } ]";
    thirdQuestion = "[ {\"type\": \"context\", \"elements\": [{"
        + " \"type\": \"mrkdwn\", \"text\": \"```" + Messages.THIRD_QUESTION + "```\" } ] } ]";
    messageAboutRules = "[{ \"type\": \"section\", \"text\": {"
        + " \"type\": \"mrkdwn\", \"text\": \"" + Messages.MESSAGE_ABOUT_RULES_1 + "\" }}, {"
        + " \"type\": \"section\", \"text\": { \"type\": \"mrkdwn\", \"text\": \"<"
        + Messages.MESSAGE_ABOUT_RULES_2 + "|Rules> :link:\" } }, { \"type\": \"section\","
        + " \"text\": {\"type\": \"mrkdwn\", \"text\": \"" + Messages.MESSAGE_ABOUT_RULES_3
        + "\"} }, {\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \""
        + Messages.MESSAGE_ABOUT_RULES_4 + "\"}]}]";
    errorWithAddingGitName = "[{\"type\": \"section\",\"text\":"
        + " {\"type\": \"mrkdwn\",\"text\": \""
        + Messages.ERROR_WITH_ADDING_GIT_NAME
        + "\n*<https://broscorp-community.slack.com/archives/D01QZ9U2GH5|Liliya Stepanovna>*\"}}]";
    estimateTheTask = "[ {\"type\": \"section\",\"text\": {\"type\":"
        + " \"mrkdwn\",\"text\": \""
        + Messages.ESTIMATE_HEADER + "\n *1*"
        + Messages.ESTIMATE_QUESTION_FIRST + "\n *2*"
        + Messages.ESTIMATE_QUESTION_SECOND + "\n *3*"
        + Messages.ESTIMATE_QUESTION_THIRD + "\n *4*"
        + Messages.ESTIMATE_QUESTION_FOURTH + "\n *5*"
        + Messages.ESTIMATE_QUESTION_FIFTH + "\n\n *"
        + Messages.ESTIMATE_FOOTER + "*\"}}]";

    //Information channels message
    messageAboutSeveralInfoChannel = "[{\"type\": \"header\","
        + "\"text\": {\"type\": \"plain_text\",\"text\": \""
        + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_1 + "\"}},{\"type\": \"context\","
        + "\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"#welcome \n "
        + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_2 + "\"}]},{\"type\": \"context\","
        + "\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"#help \n "
        + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_3 + "\"}]},{\"type\": \"context\","
        + "\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"#general \n"
        + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_4 + "\"}]},{\"type\": \"context\","
        + "\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"#random \n"
        + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_5 + "\"}]},{\"type\": \"context\","
        + "\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"#hall-of-fame \n"
        + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_6 + "\"}]},{\"type\": \"context\","
        + "\"elements\": [{\"type\": \"mrkdwn\",\"text\": \""
        + Messages.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_7 + "\"}]}]";
  }

  public static final String STATISTIC = "[{\"type\": \"header\",\t\"text\": {"
      + "\"type\":\"plain_text\",\"text\": \"Statistic:\"}},{\"type\": \"context\",\"elements\":"
      + " [{\"type\": \"mrkdwn\",\"text\": \"";
  public static final String ACTIVITY = "\"\t}]},{\"type\": \"header\",\"text\": {"
      + "\"type\": \"plain_text\",\"text\": \"Activity:\"}}";
  public static final String FINISH_PUBLISH_WEEK_STATS = ",{\"type\": \"context\",\n\"elements\":"
      + " [{\"type\": \"mrkdwn\",\t\"text\": \"*";
  public static final String LINK_PUBLISH_WEEK_STATS = "[{\"type\": \"section\", \"text\": "
      + "{\"type\": \"mrkdwn\",\"text\": \"Рейтинг этой недели доступен по ссылке: \"},"
      + "\"accessory\": {\"type\": \"button\",\"text\": {\"type\": \"plain_text\",\"text\": "
      + "\":loudspeaker:\",\"emoji\": true},\"value\": \"click_me_123\", \"url\": \"%s\","
      + "\"action_id\": \"button-action\"}},{\"type\": \"image\",\"image_url\": \"%s\","
      + "\"alt_text\": \"inspiration\"}]";
  public static final String NEXT_TASK = "[{\"type\": \"section\",\"text\":"
      + " {\"type\": \"mrkdwn\",\"text\": \"Here is your next"
      + " <https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/";
  public static final String FAILED_BUILD = "[{\"type\": \"section\", \"text\": "
      + "{ \"type\": \"mrkdwn\", \"text\": \"Oops, your build at the task <%s|%s> is down!\"}}]";
}
