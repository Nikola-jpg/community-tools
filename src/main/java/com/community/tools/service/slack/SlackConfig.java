package com.community.tools.service.slack;

import com.github.seratch.jslack.Slack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Hryhorii Perets
 */
@Configuration
public class SlackConfig {

  @Bean
  public Slack slack() {
    return Slack.getInstance();
  }
}
