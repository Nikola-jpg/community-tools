package com.community.tools.service.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.model.User;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
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
}
