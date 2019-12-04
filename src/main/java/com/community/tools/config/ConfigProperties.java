package com.community.tools.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class ConfigProperties {

  @Autowired
  private Environment environment;

  public String getToken() {
    return environment.getProperty("token");
  }

  public String getRepository() {
    return environment.getProperty("repository");
  }
}
