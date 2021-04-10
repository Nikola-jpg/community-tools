package com.community.tools.service.slack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.MethodsClient;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.model.User.Profile;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SlackServiceTest {

  @InjectMocks
  private SlackService slackService;

  @Mock
  private Slack slack;

  @Mock
  private UsersListRequest usersListRequest;


  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    //slackService = mock(SlackService.class);

  }

  @AfterEach
  void tearDown() {
  }

  @Test
  @DisplayName("Should return id by Conversation by channelName")
  void shouldGetIdByChannelName() throws Exception {

    String channelId = "testId";
    String channelName = "testUser";

    String expectedId = "testId";
    String actualId;

    when(slackService.getIdByChannelName(channelName)).thenReturn(channelId);
    actualId = slackService.getIdByChannelName(channelName);

    assertEquals(expectedId, actualId);

    verify(slackService).getIdByChannelName(channelName);

  }

  @Test
  @DisplayName("Should return id by User by username")
  void shouldGetIdByUsername() throws Exception {
    String userId = "testId";
    String username = "testUser";

    String expectedId = "testId";
    String actualId;

    MethodsClient methodsClient = mock(MethodsClient.class);

    UsersListResponse usersListResponse = mock(UsersListResponse.class);
    List<User> users = new ArrayList<>();

    User user = new User();

    Profile profile = new Profile();
    profile.setDisplayName(username);

    user.setProfile(profile);
    user.setId(userId);
    users.add(user);

    when(slack.methods(any())).thenReturn(methodsClient);

    //when(methodsClient.usersList(req -> req).getMembers()).thenReturn(users);

    when(methodsClient.usersList(usersListRequest)).thenReturn(usersListResponse);
    when(usersListResponse.getMembers()).thenReturn(users);

    actualId = slackService.getIdByUsername(username);

    assertEquals(expectedId, actualId);

    //verify(slackService).getIdByUsername(username);

  }

  @Test
  @DisplayName("Should send private message")
  void shouldSendPrivateMessage() throws Exception {

    String username = "testUser";
    String userId = "testId";

    String messageText = "testMessage";

    String timestamp = "testTimestamp";

    String expectedTimestamp = "testTimestamp";

    when(slackService.getIdByUsername(username)).thenReturn(userId);

    when(slackService.sendPrivateMessage(username, messageText)).thenReturn(timestamp);
    String actualTimestamp = slackService.sendPrivateMessage(username, messageText);

    assertEquals(expectedTimestamp, actualTimestamp);

    verify(slackService).sendPrivateMessage(username, messageText);
  }


  @Test
  @DisplayName("Should send blocks message")
  void shouldSendBlocksMessage() throws Exception {
    String username = "testUser";
    String messageText = "testMessage";

    String timestamp = "testTimestamp";

    String expectedTimestamp = "testTimestamp";

    when(slackService.sendBlocksMessage(username, messageText)).thenReturn(timestamp);
    String actualTimestamp = slackService.sendBlocksMessage(username, messageText);

    assertEquals(expectedTimestamp, actualTimestamp);

    verify(slackService).sendBlocksMessage(username, messageText);
  }

  @Test
  @DisplayName("Should send message to conversation")
  void shouldSendMessageToConversation() throws Exception {
    String channelName = "testChannel";
    String messageText = "testMessage";

    String timestamp = "testTimestamp";

    String expectedTimestamp = "testTimestamp";

    when(slackService.sendMessageToConversation(channelName, messageText)).thenReturn(timestamp);
    String actualTimestamp = slackService.sendMessageToConversation(channelName, messageText);

    assertEquals(expectedTimestamp, actualTimestamp);

    verify(slackService).sendMessageToConversation(channelName, messageText);
  }


  @Test
  @DisplayName("Should send blocks message to conversation")
  void shouldSendBlockMessageToConversation() throws Exception {
    String channelName = "testChannel";
    String messageText = "testMessage";

    String timestamp = "testTimestamp";

    String expectedTimestamp = "testTimestamp";

    when(slackService.sendBlockMessageToConversation(channelName, messageText)).thenReturn(timestamp);
    String actualTimestamp = slackService.sendBlockMessageToConversation(channelName, messageText);

    assertEquals(expectedTimestamp, actualTimestamp);

    verify(slackService).sendBlockMessageToConversation(channelName, messageText);
  }
}