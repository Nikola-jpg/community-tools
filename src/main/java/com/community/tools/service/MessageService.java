package com.community.tools.service;

public interface MessageService extends UserService {

  String sendPrivateMessage(String username, String messageText);

  <T> String sendBlocksMessage(String username, T message);

  <T> String sendAttachmentsMessage(String username, T message);

  String sendMessageToConversation(String channelName, String messageText);

  <T> String sendBlockMessageToConversation(String channelName, T message);

  String sendMessageToChat(String channelName, String messageText);


  String getIdByChannelName(String channelName);

  void sendAnnouncement(String message);

}
