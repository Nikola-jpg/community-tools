package com.community.tools.service.github;

import com.community.tools.service.PointsTaskService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.GithubAuthChecker;
import com.github.seratch.jslack.api.methods.SlackApiException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

@Service
public class GitHubHookServlet extends HttpServlet {

  @Value("${git.check.label}")
  private String labeledStr;
  @Value("${git.check.new.req}")
  private String opened;
  @Value("${spring.datasource.url}")
  private String url;
  @Value("${spring.datasource.username}")
  private String username;
  @Value("${spring.datasource.password}")
  private String password;
  @Value("${GITHUB_SECRET_TOKEN}")
  private String secret;
  @Value("${generalInformationChannel}")
  private String channel;
  @Autowired
  private SlackService service;
  @Autowired
  private GitHubGiveNewTask gitHubGiveNewTask;
  @Autowired
  private AddMentorService addMentorService;
  @Autowired
  private StateMachineService stateMachineService;
  @Autowired
  private KarmaService karmaService;
  @Autowired
  private PointsTaskService pointsTaskService;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

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
          sendNotificationMessageAboutPR(json);
          giveNewTaskIfPrOpened(json);
          addMentorIfEventIsReview(json);
          addPointIfPullLabeledDone(json);
          addKarmaForCommentApproved(json);
          checkReactionToChangeKarma(json);
        }
      }
    } catch (NoSuchAlgorithmException | InvalidKeyException | SlackApiException | SQLException e) {
      throw new RuntimeException(e);
    }

  }


  private void sendNotificationMessageAboutPR(JSONObject json)
          throws IOException, SlackApiException {
    if (json.get("action").toString().equals(opened) || checkForLabeled(json)) {
      JSONObject pull = json.getJSONObject("pull_request");
      String user = pull.getJSONObject("user").getString("login");
      String url = pull.getJSONObject("_links").getJSONObject("html").getString("href");
      if (addMentorService.doesMentorExist(user)) {
        addMentorService.sendNotifyWithMentor(user, url);
      } else {
        service
                .sendMessageToConversation(channel, "User " + user
                        + " created a pull request \n url: " + url);

      }
    }
  }

  private boolean checkForLabeled(JSONObject json) {
    if (json.get("action").toString().equals(labeledStr)) {
      List<Object> list = json.getJSONObject("pull_request").getJSONArray("labels").toList();
      return list.stream().map(o -> (HashMap) o)
              .anyMatch(e -> e.get("name").equals("ready for review"));
    }
    return false;
  }


  private void addMentorIfEventIsReview(JSONObject json) {
    if (json.get("action").equals("submitted") || checkComment(json)) {
      String mentor;
      String creator;
      try {
        mentor = json.getJSONObject("comment").getJSONObject("user").getString("login");
      } catch (JSONException e) {
        mentor = json.getJSONObject("review").getJSONObject("user").getString("login");
      }
      try {
        creator = json.getJSONObject("pull_request").getJSONObject("user").getString("login");
      } catch (JSONException e) {
        creator = json.getJSONObject("issue").getJSONObject("user").getString("login");
      }


      addMentorService.addMentor(mentor, creator);
    }
  }

  private void addPointIfPullLabeledDone(JSONObject json) {
    if (json.get("action").toString().equals(labeledStr)
        && json.getJSONObject("label").getString("name").equals("done")) {
      List<Object> list = json.getJSONObject("pull_request").getJSONArray("labels").toList();
      String sender = json.getJSONObject("sender").getString("login");
      String creator = json.getJSONObject("pull_request").getJSONObject("user").getString("login");
      String pullName = json.getJSONObject("pull_request").getString("title");
      pointsTaskService.addPointForCompletedTask(sender, creator, pullName);
    }
  }

  private void addKarmaForCommentApproved(JSONObject json) {
    boolean checkCommentApproved = false;
    String traineeReviewer = "";
    if (json.get("action").equals("created") && hasIssueAndComment(json)) {
      traineeReviewer = json.getJSONObject("comment").getJSONObject("user").getString("login");
      checkCommentApproved = json.getJSONObject("comment")
              .getString("body").toLowerCase().equals("approved");
    } else if (json.get("action").equals("submitted")) {
      traineeReviewer = json.getJSONObject("review").getJSONObject("user").getString("login");
      checkCommentApproved = json.getJSONObject("review")
              .getString("body").toLowerCase().equals("approved");
    }
    if (checkCommentApproved) {
      karmaService.changeUserKarma(traineeReviewer, 1);
    }
  }

  private void checkReactionToChangeKarma(JSONObject json) {
    if (json.get("action").equals(labeledStr)
            && json.getJSONObject("label").getString("name").equals("done")) {
      int numberOfPullRequest = json.getInt("number");
      karmaService.changeKarmaBasedOnReaction(numberOfPullRequest);
    }
  }

  private boolean hasIssueAndComment(JSONObject json) {
    boolean checkIssue = false;
    if (checkComment(json)) {
      checkIssue = json.has("issue");
    }
    return checkIssue;
  }

  private boolean checkComment(JSONObject json) {
    boolean checkComment = false;
    try {
      json.getJSONObject("comment");
      checkComment = true;
    } catch (JSONException ignored) {
      ignored.getMessage();
    }
    return checkComment;
  }

  private void giveNewTaskIfPrOpened(JSONObject json) {
    if (json.get("action").toString().equals(opened)) {
      String user = json.getJSONObject("sender").getString("login");
      gitHubGiveNewTask.giveNewTask(user);
    }
  }
}

