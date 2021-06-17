package com.community.tools.service.slack;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.community.tools.service.MessageService;
import com.community.tools.service.PublishWeekStatsService;
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.model.Conversation;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.webhook.Payload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("slack")
public class SlackService implements MessageService<String> {

  @Value("${slack.token}")
  private String token;
  @Value("${slack.webhook}")
  private String slackWebHook;

  /**
   * Send private message with messageText to username.
   *
   * @param username    Slack login
   * @param messageText Text of message
   * @throws IOException       IOException
   * @throws SlackApiException SlackApiException
   */
  @Override
  public void sendPrivateMessage(String username, String messageText) {
    Slack slack = Slack.getInstance();
    try {
      ChatPostMessageResponse postResponse =
              slack.methods(token).chatPostMessage(
                  req -> req.channel(getIdByUsername(username)).asUser(true)
                              .text(messageText));
    } catch (IOException | SlackApiException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Send block message with messageText to username.
   *
   * @param username    Slack login
   * @param message Text of message
   * @throws IOException       IOException
   * @throws SlackApiException SlackApiException
   */
  @Override
  public <T> void sendBlocksMessage(String username, T message) {
    Slack slack = Slack.getInstance();
    try {
      ChatPostMessageResponse postResponse = slack.methods(token).chatPostMessage(
          req -> req.channel(getIdByUsername(username)).asUser(true)
                      .blocksAsString((String) message));
    } catch (IOException | SlackApiException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Send attachment message with messageText to username.
   *
   * @param username    Slack login
   * @param message Text of message
   * @throws IOException       IOException
   * @throws SlackApiException SlackApiException
   */
  @Override
  public <T> void sendAttachmentsMessage(String username, T message) {
    Slack slack = Slack.getInstance();
    try {
      ChatPostMessageResponse postResponse =
          slack.methods(token).chatPostMessage(
              req -> req.channel(getIdByUsername(username)).asUser(true)
                  .attachmentsAsString((String) message));
    } catch (IOException | SlackApiException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Send attachment message with messageText to channel.
   *
   * @param channelName Name of channel
   * @param messageText Text of message
   * @throws IOException       IOException
   * @throws SlackApiException SlackApiException
   */
  @Override
  public void sendMessageToConversation(String channelName, String messageText) {
    Slack slack = Slack.getInstance();
    try {
      ChatPostMessageResponse postResponse =
          slack.methods(token).chatPostMessage(
              req -> req.channel(getIdByChannelName(channelName)).asUser(true).text(messageText));
    } catch (IOException | SlackApiException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Send attachment message with blocks of Text to the channel.
   *
   * @param channelName Name of channel
   * @param message Blocks of message
   * @throws IOException       IOException
   * @throws SlackApiException SlackApiException
   */
  @Override
  public <T> void sendBlockMessageToConversation(String channelName, T message) {
    Slack slack = Slack.getInstance();
    try {
      ChatPostMessageResponse postResponse =
          slack.methods(token).chatPostMessage(
              req -> req.channel(getIdByChannelName(channelName))
                  .asUser(true).blocksAsString((String) message));
    } catch (IOException | SlackApiException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public String nextTaskMessage(List<String> tasksList, int numberTask) {
    return MessagesToSlack.NEXT_TASK + tasksList.get(numberTask) + "|TASK>.\"}}]";
  }

  @Override
  public String ratingMessage(String url, String img) {
    return String.format(MessagesToSlack.LINK_PUBLISH_WEEK_STATS, url, img);
  }

  @Override
  public String statisticMessage(List<EventData> events) {

    StringBuilder messageBuilder = new StringBuilder();

    Map<String, List<EventData>> sortedMapGroupByActors = new HashMap<>();
    events.stream().filter(ed -> !sortedMapGroupByActors.containsKey(ed.getActorLogin()))
        .forEach(ed -> sortedMapGroupByActors.put(ed.getActorLogin(), new ArrayList<>()));

    messageBuilder.append("[{\"type\": \"header\",\t\"text\": {\"type\":"
        + " \"plain_text\",\"text\": \"Statistic:\"}},"
        + "{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\", \"text\": \"");
    events.stream()
        .collect(Collectors.groupingBy(EventData::getType))
        .entrySet().stream()
        .sorted(Comparator
            .comparingInt((Entry<Event, List<EventData>> entry)
                -> entry.getValue().size()).reversed())
        .forEach(entry -> {
          entry.getValue().forEach(e -> sortedMapGroupByActors.get(e.getActorLogin()).add(e));
          messageBuilder.append("\n");
          messageBuilder.append(PublishWeekStatsService.getTypeTitleBold(entry.getKey()))
              .append(PublishWeekStatsService.emojiGen(entry.getKey()));
          messageBuilder.append(": ");
          messageBuilder.append(entry.getValue().size());

        });
    messageBuilder.append("\"\t}]},{\"type\": \"header\",\"text\": "
        + "{\"type\": \"plain_text\",\"text\": \"Activity:\"}}");
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
          messageBuilder.append(",{\"type\": \"context\",\n"
              + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"*");

          messageBuilder.append(name.getKey());
          messageBuilder.append("*: ");
          messageBuilder.append(authorsActivMessage);
          messageBuilder.append("\"}]}");
        });
    messageBuilder.append("]");

    return messageBuilder.toString();
  }

  /**
   * Get Conversation by Slack`s channelName.
   *
   * @param channelName Slack`s channelName
   * @return id of Conversation
   */
  @Override
  public String getIdByChannelName(String channelName) {
    Slack slack = Slack.getInstance();
    try {
      Conversation channel = slack.methods(token)
          .conversationsList(req -> req)
          .getChannels()
          .stream()
          .filter(u -> u.getName().equals(channelName))
          .findFirst().get();

      return channel.getId();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get user by Slack`s id.
   *
   * @param id Slack`s id
   * @return realName of User
   */
  @Override
  public String getUserById(String id) {
    Slack slack = Slack.getInstance();
    try {
      User user = slack.methods(token).usersList(req -> req).getMembers().stream()
              .filter(u -> u.getId().equals(id))
              .findFirst().get();
      return user.getProfile().getDisplayName();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get user by Slack`s id.
   *
   * @param id Slack`s id
   * @return Slack`s id
   */
  @Override
  public String getIdByUser(String id) {
    Slack slack = Slack.getInstance();
    try {
      User user = slack.methods(token).usersList(req -> req).getMembers().stream()
              .filter(u -> u.getRealName().equals(id))
              .findFirst().get();
      return user.getId();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get user by Slack`s username.
   *
   * @param username Slack`s id
   * @return Slack`s id
   */
  @Override
  public String getIdByUsername(String username) {
    Slack slack = Slack.getInstance();
    try {
      User user = slack.methods(token).usersList(req -> req).getMembers().stream()
          .filter(u -> u.getProfile().getDisplayName().equals(username))
          .findFirst().get();
      return user.getId();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get all Slack`s user.
   *
   * @return Set of users.
   */
  @Override
  public Set<User> getAllUsers() {
    try {
      Slack slack = Slack.getInstance();
      Set<User> users = new HashSet<>(slack.methods()
              .usersList(UsersListRequest.builder()
                      .token(token)
                      .build())
              .getMembers());

      return users;
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Send announcement with message.
   *
   * @param message Text of message
   */
  @Override
  public void sendAnnouncement(String message) {
    try {
      Payload payload = Payload.builder().text(message).build();
      Slack slack = Slack.getInstance();
      slack.send(slackWebHook, payload);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}