package com.community.tools.service;

import com.community.tools.model.UsedPlatforms;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockService {

  @Value("${spring.profiles.active}")
  private UsedPlatforms activePlatform;

  /**
   * Selected message for the active service.
   * @param messages array messages for different services
   * @param <T> type messages
   * @return block message
   */
  public <T> T createBlockMessage(T... messages) {
    switch (activePlatform) {
      case slack: {
        for (T message : messages) {
          if (message instanceof String) {
            return message;
          }
        }
        break;
      }
      case discord: {
        for (T message : messages) {
          if (message instanceof MessageEmbed) {
            return message;
          }
        }
        break;
      }
      default:
    }
    throw new UnsupportedOperationException("This message block is not supported.");
  }
}
