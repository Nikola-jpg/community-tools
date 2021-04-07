package com.community.tools.service.slack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.Methods;
import com.github.seratch.jslack.api.methods.MethodsClient;
import com.github.seratch.jslack.api.methods.impl.MethodsClientImpl;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.model.User.Profile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Hryhorii Perets
 */
class SlackServiceTest {

  @InjectMocks
  private SlackService slackService;

  @Mock
  private Slack slack;

  @Mock
  private MethodsClient methodsClient;

  @Mock
  private UsersListResponse usersListResponse;


  @BeforeEach
  void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  @DisplayName("Should return id by Conversation by channelName")
  void shouldGetIdByChannelName() {
  }

  @Test
  @DisplayName("Should return id by User by username")
  void shouldGetIdByUsername() throws Exception {
    Profile profile = new Profile();
    profile.setDisplayName("testUser");

    User user = new User();
    user.setId("testId");
    user.setProfile(profile);

    List<User> userList = new ArrayList<>();
    userList.add(user);

    String expectedId = "testId";
    String token = "";
    String username = "testUser";

    when(slack.methods(token)).thenReturn(methodsClient);
    when(methodsClient.usersList(req -> req)).thenReturn(usersListResponse);
    when(usersListResponse.getMembers()).thenReturn(userList);

    String actualId = usersListResponse.getMembers().stream()
        .filter(u -> u.getProfile().getDisplayName().equals(username))
        .findFirst().get().getId();

    assertEquals(expectedId, actualId);
  }

}