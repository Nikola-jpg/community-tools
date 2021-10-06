package com.community.tools.controller;

import com.community.tools.dto.UserTasksStatusDto;
import com.community.tools.model.User;
import com.community.tools.service.TaskStatusService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UsersRestController {

  @Autowired
  private TaskStatusService taskStatusService;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @GetMapping
  public List<User> getUsers() {
    List<UserTasksStatusDto> userTasksStatusDtoList = new ArrayList<>();

    List<User> users = taskStatusService.addPlatformNameToUser(1, "gitName", "asc");
    users.forEach(user -> {
      userTasksStatusDtoList.add(UserTasksStatusDto.fromUser(user, tasksForUsers));
    });

    return users;
  }


}
