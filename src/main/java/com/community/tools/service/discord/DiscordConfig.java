package com.community.tools.service.discord;

import com.community.tools.service.discord.listener.Listener;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DiscordConfig {

  @Value("${discord.token}")
  private String token;

  @Bean
  public JDA jda() {
    try {
      JDABuilder builder = JDABuilder.createDefault(token);
      builder.addEventListeners(new Listener());
      return builder.build();
    } catch (LoginException exception) {
      throw new RuntimeException(exception);
    }
  }

}
