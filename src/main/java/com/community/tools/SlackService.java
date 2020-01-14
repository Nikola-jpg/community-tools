package com.community.tools;

import com.github.seratch.jslack.*;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.model.Channel;
import com.github.seratch.jslack.api.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SlackService {

  @Value("${slack.token}")
  private String token;

  public String sendPrivateMessage(String username, String messageText)
      throws IOException, SlackApiException {
    Slack slack = Slack.getInstance();

    User user = slack.methods(token).usersList(req -> req).getMembers().stream()
        .filter(u -> u.getProfile().getDisplayName().equals(username))
        .findFirst().get();

    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(user.getId()).asUser(true).text(messageText));

    return postResponse.getTs();
  }

  public String sendMessageToChat(String channelName, String messageText)
      throws IOException, SlackApiException {
    Slack slack = Slack.getInstance();

    Channel channel = slack.methods(token)
        .channelsList(req -> req)
        .getChannels()
        .stream()
        .filter(u -> u.getName().equals(channelName))
        .findFirst().get();

    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(channel.getId()).asUser(true).text(messageText));

    return postResponse.getTs();
  }

}