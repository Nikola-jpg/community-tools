package com.community.tools.service;

public interface MessageService<T> extends UserService {

  void sendPrivateMessage(String username, String messageText);

  void sendBlocksMessage(String username, T message);

  void sendAttachmentsMessage(String username, T message);

  void sendMessageToConversation(String channelName, String messageText);

  void sendBlockMessageToConversation(String channelName, T message);

  String getIdByChannelName(String channelName);

  void sendAnnouncement(String message);

}
