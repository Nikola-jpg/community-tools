package com.community.tools.service;

import com.community.tools.dto.UserDto;
import java.util.Map;
import java.util.Set;

public interface MessageService<T> {

  void sendPrivateMessage(String username, String messageText);

  void sendBlocksMessage(String username, T message);

  void sendAttachmentsMessage(String username, T message);

  void sendMessageToConversation(String channelName, String messageText);

  void sendBlockMessageToConversation(String channelName, T message);

  void sendAnnouncement(String message);

  String getIdByChannelName(String channelName);

  String getUserById(String id);

  String getIdByUser(String id);

  String getIdByUsername(String username);

  Set<UserDto> getAllUsers();

  Map<String, String> getIdWithName();

}
