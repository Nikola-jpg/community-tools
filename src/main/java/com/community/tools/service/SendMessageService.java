package com.community.tools.service;

import com.github.seratch.jslack.api.model.User;
import java.util.Set;

/**
 * @author Hryhorii Perets
 */
public interface SendMessageService {

  String sendPrivateMessage(String username, String messageText);

  String sendBlocksMessage(String username, String messageText);

  String sendAttachmentsMessage(String username, String messageText);

  String sendMessageToConversation(String channelName, String messageText);

  String sendBlockMessageToConversation(String channelName, String messageText);

  String sendMessageToChat(String channelName, String messageText);


  String getUserById(String id);

  String getIdByUser(String id);

  Set<User> getAllUsers();

}
