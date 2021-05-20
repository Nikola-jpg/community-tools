package com.community.tools.service;

public interface MessageService extends UserService {

  void sendPrivateMessage(String username, String messageText);

  <T> void sendBlocksMessage(String username, T message);

  <T> void sendAttachmentsMessage(String username, T message);

  void sendMessageToConversation(String channelName, String messageText);

  <T> void sendBlockMessageToConversation(String channelName, T message);

  <T> T createBlockMessage(T... messages);

  String getIdByChannelName(String channelName);

  void sendAnnouncement(String message);

}
