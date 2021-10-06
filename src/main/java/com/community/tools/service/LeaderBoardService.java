package com.community.tools.service;

import com.community.tools.model.User;
import com.community.tools.service.github.GitHubService;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaderBoardService {

  @Autowired
  StateMachineRepository stateMachineRepository;


  @Autowired
  private MessageService messageService;

  @Autowired
  private GitHubService gitHubService;



  /**
   * This method load slack users and add slackName to the User model.
   * @return List of Users.
   */
  public List<User> addSlackNameToUser() {
    List<User> list = stateMachineRepository.findAll();
    Set<com.github.seratch.jslack.api.model.User> slackUsers = messageService.getAllUsers();
    Map<String, String> map = slackUsers.stream()
            .filter(u -> u.getRealName() != null)
            .collect(Collectors.toMap(user -> user.getId(), user -> user.getRealName()));
    for (User user: list) {
      String slackName = map.get(user.getUserID());
      user.setPlatformName(slackName);
    }
    return list;
  }

  /**
   * This method get active users from period in days
   * and add their to the User model.
   * @param days Period in days.
   * @return List of Users.
   */
  public  List<User> getActiveUsersFromPeriod(int days)  {
    LocalDate tempDate = LocalDate.now().minusDays(days);
    Date date = Date.from(tempDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    List<User> list = addSlackNameToUser();
    Set<String> userNames = gitHubService.getActiveUsersFromGit(date);
    List<User> userList = list.stream()
            .filter(user -> userNames
                    .contains(user.getGitName())).collect(Collectors.toList());
    return userList;
  }
}
