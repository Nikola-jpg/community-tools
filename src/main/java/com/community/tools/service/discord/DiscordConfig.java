package com.community.tools.service.discord;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DiscordConfig {

  @Value("${discord.token}")
  private String token;

  /**
   * Created and configure object JDA.
   * @return object JDA
   */
  @Bean
  public JDA jda() {
    try {
      JDABuilder builder = JDABuilder.createDefault(token);
      builder.setChunkingFilter(ChunkingFilter.ALL);
      builder.setMemberCachePolicy(MemberCachePolicy.ALL);
      builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
      builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
      builder.setBulkDeleteSplittingEnabled(false);
      builder.setCompression(Compression.NONE);
      builder.setActivity(Activity.playing("Discord"));
      builder.addEventListeners(new DiscordEventListener());
      return builder.build();
    } catch (LoginException exception) {
      throw new RuntimeException(exception);
    }
  }

}
