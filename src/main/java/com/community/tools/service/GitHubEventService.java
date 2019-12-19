package com.community.tools.service;

import com.community.tools.model.Event;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.kohsuke.github.GHEvent;
import org.kohsuke.github.GHEventInfo;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitHubEventService {

  @Autowired
  GitHubConnectService service;

  public List<Event> getEvents(String startDate, String endDate) {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    try {
      Date start = format.parse(startDate);
      Date end = format.parse(endDate);

      List<Event> list = new ArrayList<>();
      GHRepository repository = service.getGitHubRepository();
      List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.ALL);
      PagedIterable<GHEventInfo> ghEventInfos = repository.listEvents();

      for (GHEventInfo info : ghEventInfos) {
        for (GHPullRequest pullRequest : pullRequests) {
          Date createdAt = info.getCreatedAt();
          String actorLogin = info.getActorLogin();
          String state = pullRequest.getState().toString();
          GHEvent type = info.getType();
          if (createdAt.before(end) && createdAt.after(start)) {
            if (type == GHEvent.PULL_REQUEST || type == GHEvent.PULL_REQUEST_REVIEW_COMMENT) {
              list.add(new Event(createdAt, actorLogin, type.toString(), state));
            }
          }
        }
      }
      return list;
    } catch (IOException | ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
