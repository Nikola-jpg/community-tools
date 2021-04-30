package com.community.tools.model;

import com.google.common.base.Splitter;
import java.util.Map;
import java.util.stream.Stream;

public class Messages {

  //messages for bot
  public static final String ANSWERED_NO_DURING_VERIFICATION = "Oops, sorry :neutral_face:"
      + " Check your nickname correctly and try again, please :hugging_face:";
  public static final String ASK_ABOUT_PROFILE = "Is it your profile? (*yes*/*no*)";
  public static final String CHECK_NICK_NAME = "Okay! Let me check your nick,";
  public static final String CONGRATS_AVAILABLE_NICK = "Hurray! Your nick is available."
      + " Nice to meet you :smile:";
  public static final String DO_NOT_UNDERSTAND_WHAT_TODO = "I do not understand what you want,"
      + " please call the admin!";
  public static final String FAILED_CHECK_NICK_NAME = "Sry but looks like you are still not added"
      + " to our team in Git :worried:";
  public static final String FAILED_NICK_NAME = "Sry but looks like you are not registered"
      + " on Github :worried:";
  public static final String GET_FIRST_TASK = "This is your first"
      + " <https://github.com/Broscorp-net/traineeship/"
      + "tree/master/module1/src/main/java/net/broscorp/checkstyle|TASK>. gl";
  public static final String LAST_TASK = "You are so cool! :tada: Just one step away from"
      + " finishing your set of tasks. Make your last task, send it and wait for the review."
      + " Also you can help your colleagues on their journey :hugging_face:";
  public static final String USERS_AGREE_MESSAGE = "I agree";
  public static final String WELCOME = "Welcome to the club buddy :handshake:";
  public static final String ADD_GIT_NAME = "So, that's it. You've read through all our rules."
      + " \n If you're ready to accept this challenge enter your GitHub nickname.";
  public static final String NO_ONE_CASE = "NO ONE CASE";
  public static final String NOT_THAT_MESSAGE = "Please answer the latest message :ghost:";
  public static final String ABILITY_REVIEW_MESSAGE = "Well done! Now you can review tasks your"
      + " colleagues :sunglasses: \n You can leave comments on those tasks where you have"
      + " the marking label done (the task is accepted).\n\n If you think that it is necessary"
      + " to make changes, write a comment and install the label *changes request*."
      + " When you and your colleague decide that the changes applied and the task is done,"
      + " you should write a comment *approved*. \n\n If in your opinion everything is ok to write"
      + " a comment approved. You can specify what was done well. :relaxed:  \n"
      + " For the quality reviews, you can get + into karma :smirk:";
  public static final String ZERO_POINTS_MESSAGE = "For the task \"pull_name\" you got 0 points."
      + " You made a mistake in the name of the pull request, from now on be more careful.";
  public static final String DEFAULT_MESSAGE = "Oops, I'm sorry, but I don't have an answer"
      + " to your request.";
  public static final String FIRST_QUESTION = "1. What names should branches"
      + " and pull requests have?";
  public static final String SECOND_QUESTION = "2. Which library should be used for unit testing?";
  public static final String THIRD_QUESTION = "3. Your task labelled as \"changes requested\"."
      + " What is your next step?";
  public static final String MESSAGE_ABOUT_RULES_1 = "Before you start, be sure to familiarize"
      + " yourself with the rules.  You can read the rules by clicking on this link.:point_down:";
  public static final String MESSAGE_ABOUT_RULES_2 =
      "<https://github.com/Broscorp-net/traineeship/blob/master/README.md>";
  public static final String MESSAGE_ABOUT_RULES_3 = "After you have read the rules,"
      + " please answer a few questions.";
  public static final String MESSAGE_ABOUT_RULES_4 = "`Let me know when you are ready"
      + " (write \"ready\").`";



  //Information channels message
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_1 = "We have several channels"
      + " to help you during your internship:";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_2 = "1. #welcome \n Please write"
      + " a few words about yourself) \n Yeees, perhaps you will work with these people"
      + " in one team :slightly_smiling_face: ";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_3 = "2. #help \n You can ask"
      + " questions on any subject and get an answer :nerd_face: ";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_4 = "3. #general \n Channel for"
      + " important messages and various announcements:rolled_up_newspaper::bookmark_tabs: ";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_5 = "4. #random \n A channel for"
      + " communication on any topics :yum: ";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_6 = "5. #hall-of-fame \n Stories"
      + " of those who have completed an internship and found a job :sunglasses: ";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_7 = "`Please use the trades to"
      + " respond to messages.`";
  public static final String[] TASKS_FOR_USERS = {"checkstyle", "primitives", "boxing", "valueref",
      "equals/hashcode", "patform", "bytecode", "gc", "exceptions", "classpath", "generics",
      "inner/classes", "override/overload", "strings", "gamelife"};
  public static final Map<String, String> POINTS_FOR_TASK = Splitter.on(", ")
      .withKeyValueSeparator(":").split("checkstyle:1, primitives:2, boxing:2, valueref:3, "
      + "equals/hashcode:3, platform:3, bytecode:2, gc:4, exceptions:4, classpath:3, generics:5, "
      + "inner/classes:5, override/overload:4, strings:5, gamelife:10");
  public static final String NO_ACTIVITY_MESSAGE = "There was no activity last week :pensive:";

}
