package com.community.tools.service;

public interface MessageService extends UserService {

  String sendPrivateMessage(String username, String messageText);

  <T> String sendBlocksMessage(String username, T message);

  String sendAttachmentsMessage(String username, String messageText);

  String sendMessageToConversation(String channelName, String messageText);

  String sendBlockMessageToConversation(String channelName, String messageText);

  String sendMessageToChat(String channelName, String messageText);


  String getIdByChannelName(String channelName);

  void sendAnnouncement(String message);

}
