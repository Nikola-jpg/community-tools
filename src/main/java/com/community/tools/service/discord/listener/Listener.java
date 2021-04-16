package com.community.tools.service.discord.listener;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Listener extends ListenerAdapter {

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    super.onReady(event);
    log.info("{} is ready", event.getJDA().getSelfUser());

  }
}
