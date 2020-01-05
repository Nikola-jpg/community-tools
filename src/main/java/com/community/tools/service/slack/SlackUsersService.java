package com.community.tools.service.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.model.User;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SlackUsersService {

  private final SlackService service;

  public Set<String> getAllUsers() {
    try {
      Slack slack = Slack.getInstance();
      Set<String> users = slack.methods()
          .usersList(UsersListRequest.builder()
              .token(service.getToken())
              .build())
          .getMembers().stream().map(User::getName).collect(Collectors.toSet());

      return users;
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
