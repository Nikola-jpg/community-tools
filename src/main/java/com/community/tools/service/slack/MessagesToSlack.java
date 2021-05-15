package com.community.tools.service.slack;

import com.community.tools.model.Messages;

public class MessagesToSlack {

  //messages for bot
  public static final String GET_FIRST_TASK = "[{\"type\": \"section\",\"text\": {"
      + "\"type\":\"mrkdwn\",\"text\": \"" + Messages.CONGRATS_AVAILABLE_NICK + "\n\n"
      + Messages.GET_FIRST_TASK + " <"
      + Messages.LINK_FIRST_TASK + "|TASK> :link:\"}}]";
  public static final String ADD_GIT_NAME = "[{\"type\": \"section\",\"text\": {"
      + "\"type\":\"mrkdwn\",\"text\": \"" + Messages.ADD_GIT_NAME + "\"}}]";
  public static final String NO_ONE_CASE = "[{\"type\": \"divider\"},{\"type\": \"section\","
      + "\"text\": {\"type\": \"mrkdwn\",\"text\": \"" + Messages.NO_ONE_CASE + "\"},"
      + "\"accessory\": {\"type\": \"button\",\"text\": {\"type\": \"plain_text\","
      + "\"text\": \"Button\",\"emoji\": true},\"value\": \"Button\"}}]";
  public static final String NOT_THAT_MESSAGE = "[{\"type\": \"section\",\"text\": {"
      + "\"type\": \"mrkdwn\",\"text\": \"" + Messages.NOT_THAT_MESSAGE + "\"}}]";
  public static final String ABILITY_REVIEW_MESSAGE = "[{\"type\": \"context\","
      + " \"elements\": [{\"type\": \"mrkdwn\",\"text\": \""
      + Messages.ABILITY_REVIEW_MESSAGE + "\"}]}]";
  public static final String FIRST_QUESTION = "[ {\"type\": \"context\", \"elements\": [{"
      + " \"type\": \"mrkdwn\", \"text\": \"```" + Messages.FIRST_QUESTION + "```\" } ] } ]";
  public static final String SECOND_QUESTION = "[ {\"type\": \"context\", \"elements\": [{"
      + " \"type\": \"mrkdwn\", \"text\": \"```" + Messages.SECOND_QUESTION + "```\" } ] } ]";
  public static final String THIRD_QUESTION = "[ {\"type\": \"context\", \"elements\": [{"
      + " \"type\": \"mrkdwn\", \"text\": \"```" + Messages.THIRD_QUESTION + "```\" } ] } ]";
  public static final String MESSAGE_ABOUT_RULES = "[{ \"type\": \"section\", \"text\": {"
      + " \"type\": \"mrkdwn\", \"text\": \"" + Messages.MESSAGE_ABOUT_RULES_1 + "\" }}, {"
      + " \"type\": \"section\", \"text\": { \"type\": \"mrkdwn\", \"text\": \""
      + Messages.MESSAGE_ABOUT_RULES_2 + "|Rules> :link:\" } }, { \"type\": \"section\","
      + " \"text\": {\"type\": \"mrkdwn\", \"text\": \"" + Messages.MESSAGE_ABOUT_RULES_3
      + "\"} }, {\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \""
      + Messages.MESSAGE_ABOUT_RULES_4 + "\"}]}]";
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
  public static final String CHECK_NEXT_TASK = "[{\"type\": \"section\",\"text\":"
      + " {\"type\": \"mrkdwn\",\"text\": \"" + Messages.NEXT_TASK;
  public static final String ERROR_WITH_ADDING_GIT_NAME = "[{\"type\": \"section\",\"text\":"
      + " {\"type\": \"mrkdwn\",\"text\": \""
      + Messages.ERROR_WITH_ADDING_GIT_NAME
      + "\n*<https://broscorp-community.slack.com/archives/D01QZ9U2GH5|Liliya Stepanovna>*\"}}]";

  //Information channels message
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL = "[{\"type\": \"header\","
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
