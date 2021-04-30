package com.community.tools.service;

import com.community.tools.service.discord.DiscordService;
import com.community.tools.service.slack.SlackService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockService {

  //private final MessageService messageService;

  @Autowired
  private Map<String, MessageService> messageServiceMap;

  @Value("${currentMessageService}")
  private String currentMessageService;

  /**
   * Selected current message service.
   * @return current message service
   */
  public MessageService getMessageService() {
    return messageServiceMap.get(currentMessageService);
  }

  /**
   * Selected message for the active service.
   * @param messages array messages for different services
   * @param <T> type messages
   * @return block message
   */
  public <T> T createBlockMessage(T... messages) {
    if (getMessageService() instanceof SlackService) {
      for (T message: messages) {
        if (message instanceof String) {
          return message;
        }
      }
    }
    if (getMessageService() instanceof DiscordService) {
      for (T message: messages) {
        if (message instanceof MessageEmbed) {
          return message;
        }
      }
    }
    throw new UnsupportedOperationException("This message block is not supported.");
  }
}
