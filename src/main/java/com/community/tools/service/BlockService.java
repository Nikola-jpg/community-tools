package com.community.tools.service;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockService {

  @Value("${currentMessageService}")
  private String currentMessageService;

  /**
   * Selected message for the active service.
   * @param messages array messages for different services
   * @param <T> type messages
   * @return block message
   */
  public <T> T createBlockMessage(T... messages) {
    switch (currentMessageService) {
      case "slackService": {
        for (T message : messages) {
          if (message instanceof String) {
            return message;
          }
        }
        break;
      }
      case "discordService": {
        for (T message : messages) {
          if (message instanceof MessageEmbed) {
            return message;
          }
        }
        break;
      }
      default:
        throw new UnsupportedOperationException("This message block is not supported.");
    }
    throw new UnsupportedOperationException("This message block is not supported.");
  }
}
