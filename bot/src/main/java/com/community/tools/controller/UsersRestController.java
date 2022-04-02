package com.community.tools.controller;

import com.community.tools.model.User;
import com.community.tools.service.LeaderBoardService;
import com.community.tools.service.TaskStatusService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UsersRestController {

  @Autowired
  private TaskStatusService taskStatusService;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @Autowired
  LeaderBoardService leaderBoardService;

  /**
   * Request controller for handing api requests.
   *
   * @param userLimit query param to limit showed users
   * @param daysFetch query param to limit users by recent activity
   * @param sort      query param to sort by field
   * @return returns json with users from db according to query params
   */
  @GetMapping
  @Transactional
  public List<User> getUsers(@RequestParam(required = false) Integer userLimit,
      @RequestParam(defaultValue = "30") Integer daysFetch,
      @RequestParam(required = false) String sort) {

    List<User> users = leaderBoardService.getActiveUsersFromPeriod(daysFetch);
    users = taskStatusService.addPlatformNameToSelectedUsers(users);

    Comparator<User> comparator = (o1, o2) -> {
      if (o1.getDateRegistration() == null && o2.getDateRegistration() == null) {
        return 0;
      } else if (o1.getDateRegistration() == null) {
        return 1;
      } else if (o2.getDateRegistration() == null) {
        return -1;
      } else {
        return o2.getDateRegistration().compareTo(o1.getDateRegistration());
      }
    };

    List<User> newUsers = new ArrayList<>(users);
    newUsers.sort(comparator);

    for (User u : newUsers) {
      if (u.getDateRegistration() != null) {
        u.setDateRegistrationFront(convertDateToString(u.getDateRegistration()));
      }
      if (u.getDateLastActivity() != null) {
        u.setDateLastActivityFront(convertDateToString(u.getDateLastActivity()));
      }
    }

    if (userLimit != null) {
      return newUsers.subList(0, userLimit);
    } else {
      return newUsers;
    }
  }

  public String convertDateToString(Date date) {
    return date.toInstant().toString().substring(0, 10);
  }

}