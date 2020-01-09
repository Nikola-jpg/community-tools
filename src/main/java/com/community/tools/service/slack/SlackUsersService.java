package com.community.tools.service.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.model.User;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SlackUsersService {

  private final SlackService service;

  public Set<User> getAllUsers() {
    try {
      Slack slack = Slack.getInstance();
      Set<User> users = new HashSet<>(slack.methods()
          .usersList(UsersListRequest.builder()
              .token(service.getToken())
              .build())
          .getMembers());

      return users;
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
