package com.community.tools.service;

import com.github.seratch.jslack.api.model.User;
import java.util.Set;


public interface SendMessageService {

  String sendPrivateMessage(String username, String messageText);

  String sendBlocksMessage(String username, String messageText);

  String sendAttachmentsMessage(String username, String messageText);

  String sendMessageToConversation(String channelName, String messageText);

  String sendBlockMessageToConversation(String channelName, String messageText);

  String sendMessageToChat(String channelName, String messageText);


  String getIdByChannelName(String channelName);

  String getUserById(String id);

  String getIdByUser(String id);

  String getIdByUsername(String username);

  Set<User> getAllUsers();

  void sendAnnouncement(String message);

}
