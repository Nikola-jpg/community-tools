package com.community.tools.service;

import com.community.tools.service.discord.DiscordService;
import com.community.tools.service.slack.SlackService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockService {

  private final MessageService messageService;

  /**
   * Selected message for the active service.
   * @param messages array messages for different services
   * @param <T> type messages
   * @return block message
   */
  public <T> T createBlockMessage(T... messages) {
    if (messageService instanceof SlackService) {
      for (T message: messages) {
        if (message instanceof String) {
          return message;
        }
      }
    }
    if (messageService instanceof DiscordService) {
      for (T message: messages) {
        if (message instanceof MessageEmbed) {
          return message;
        }
      }
    }
    throw new UnsupportedOperationException("This message block is not supported.");
  }
}
