package com.community.tools.controller;

import com.community.tools.service.github.GitHubHookService;
import com.community.tools.util.GithubAuthChecker;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gitHook")
public class GitHubHookController {

  @Value("${spring.datasource.url}")
  private String url;
  @Value("${spring.datasource.username}")
  private String username;
  @Value("${spring.datasource.password}")
  private String password;
  @Value("${GITHUB_SECRET_TOKEN}")
  private String secret;
  @Autowired
  private GitHubHookService gitHubHookService;

  /**
   * Method receive webhook data from GitHub.
   * @param req HttpServletRequest
   * @param resp HttpServletResponse
   * @throws IOException IOException
   */
  @PostMapping
  public void getHookData(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    StringBuilder builder = new StringBuilder();
    String str = "";

    while ((str = req.getReader().readLine()) != null) {
      builder.append(str);
    }
    JSONObject json = new JSONObject(builder.toString());

    try {
      if (new GithubAuthChecker(secret)
              .checkSignature(req.getHeader("X-Hub-Signature"), builder.toString())) {

        SingleConnectionDataSource connect = new SingleConnectionDataSource();
        connect.setUrl(url);
        connect.setUsername(username);
        connect.setPassword(password);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connect);
        String sql = "INSERT INTO public.\"GitHookData\" (time, jsonb_data) VALUES (? , ?)";
        Date date = new Date();
        PGobject out = new PGobject();
        out.setType("json");
        out.setValue(json.toString());
        jdbcTemplate.update(sql, date, out);
        boolean actionExist = false;
        try {
          json.get("action");
          actionExist = true;
        } catch (JSONException ignored) {
          ignored.getMessage();
        }

        if (actionExist) {
          gitHubHookService.doActionsAfterReceiveHook(json);
        }
      }
    } catch (NoSuchAlgorithmException | InvalidKeyException | SQLException e) {
      throw new RuntimeException(e);
    }

  }



}

