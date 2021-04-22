package com.community.tools.service;

import com.community.tools.service.discord.BlockField;
import com.community.tools.service.discord.FieldName;
import java.util.List;
import java.util.Map;

public interface MessageService extends UserService {

  String sendPrivateMessage(String username, String messageText);

  String sendBlocksMessage(String username, String messageText);

  //Probe method realisation
  String sendBlocksMessage(String username, List<BlockField> fields);

  String sendAttachmentsMessage(String username, String messageText);

  String sendMessageToConversation(String channelName, String messageText);

  String sendBlockMessageToConversation(String channelName, String messageText);

  String sendMessageToChat(String channelName, String messageText);


  String getIdByChannelName(String channelName);

  void sendAnnouncement(String message);

}
