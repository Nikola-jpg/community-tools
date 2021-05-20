package com.community.tools.service.discord;

import com.community.tools.service.StateMachineService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DiscordEventListener extends ListenerAdapter {

  @Autowired
  private StateMachineService stateMachineService;

  @Value("${testModeSwitcher}")
  private Boolean testModeSwitcher;

  @Override
  public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
    try {
      String userId = event.getUser().getId();
      stateMachineService.resetUser(userId);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
    if (!event.getAuthor().isBot()) {
      String messageFromUser = event.getMessage().getContentRaw();
      String userId = event.getAuthor().getId();
      try {
        if (messageFromUser.equalsIgnoreCase("reset")
            && testModeSwitcher) {
          stateMachineService.resetUser(userId);
        } else {
          stateMachineService.doAction(messageFromUser, userId);
        }
      } catch (Exception exception) {
        throw new RuntimeException("Impossible to answer request with id = "
            + event.getAuthor().getId(), exception);
      }
    }
  }

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    super.onReady(event);
    log.info("{} is ready", event.getJDA().getSelfUser());
  }
}