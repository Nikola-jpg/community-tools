package com.community.tools.service.discord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DiscordEventListener extends ListenerAdapter {

  @Override
  public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
    event.getUser().openPrivateChannel().queue((channel) -> {
      channel.sendMessageFormat("Welcome").queue();
    });
  }


  @Override
  public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
    if(event.getMessage().getContentRaw().equalsIgnoreCase("#help")) {
      event.getAuthor().openPrivateChannel().queue((channel) -> {
        channel.sendMessageFormat("How can i help you?").queue();
      });
    }
  }

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    super.onReady(event);
    log.info("{} is ready", event.getJDA().getSelfUser());

  }

}
