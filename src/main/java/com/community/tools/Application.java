package com.community.tools;

import com.community.tools.util.ConvertUnicode;
import java.util.HashMap;
import java.util.Properties;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

  /**
   * Main method of Spring application.
   * @param args args
   */
  public static void main(String[] args) {
    new SpringApplicationBuilder(Application.class)
            .properties(props())
            .build()
            .run(args);
  }

  /**
   * Add some property from file. Cyrillic text convert to Unicode. As example, file "property.txt".
   * @return Properties
   */
  @SneakyThrows
  private static Properties props() {
    ConvertUnicode convert = new ConvertUnicode();
    HashMap<String,String> prop = convert.convertToUnicode("src/main/resources/property.txt");
    Properties properties = new Properties();
    properties.putAll(prop);
    return properties;
  }
}
