package com.community.tools.service.github;

import java.sql.SQLException;
import java.util.Date;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import org.springframework.stereotype.Service;

@Service
public class GitHookDataService {
  @Value("${spring.datasource.url}")
  private String url;
  @Value("${spring.datasource.username}")
  private String username;
  @Value("${spring.datasource.password}")
  private String password;
  private final String sql = "INSERT INTO public.\"GitHookData\" (time, jsonb_data) VALUES (? , ?)";

  /**
   * Save JSON from GitHub to GitHookData table into Database.
   * @param json Json from GitHub
   */
  public void saveDataIntoDB(JSONObject json) {
    SingleConnectionDataSource connect = new SingleConnectionDataSource();
    connect.setUrl(url);
    connect.setUsername(username);
    connect.setPassword(password);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(connect);
    Date date = new Date();
    PGobject out = new PGobject();
    out.setType("json");
    try {
      out.setValue(json.toString());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    jdbcTemplate.update(sql, date, out);
    connect.destroy();
  }
}
