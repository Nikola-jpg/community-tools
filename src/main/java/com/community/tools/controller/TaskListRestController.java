package com.community.tools.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tasks")
public class TaskListRestController {

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @GetMapping
  public String[] getTasksForUsers(){
    return tasksForUsers;
  }
}
