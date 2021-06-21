package com.community.tools.service;

import com.community.tools.model.EventData;
import java.util.List;

public interface MessageService<T> extends UserService {

  void sendPrivateMessage(String username, String messageText);

  <T> void sendBlocksMessage(String username, T message);

  <T> void sendAttachmentsMessage(String username, T message);

  void sendMessageToConversation(String channelName, String messageText);

  <T> void sendBlockMessageToConversation(String channelName, T message);

  T nextTaskMessage(List<String> tasksList, int numberTask);

  T infoLinkMessage(String info, String url, String img);

  T statisticMessage(List<EventData> events);

  String getIdByChannelName(String channelName);

  void sendAnnouncement(String message);

}
