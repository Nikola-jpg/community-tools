package com.community.tools.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.community.tools.service.github.jpa.Mentors;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.util.ReflectionTestUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PointsTaskServiceTest {

  @InjectMocks
  private PointsTaskService pointsTaskService;

  @Mock
  private SlackService slackService;

  @Mock
  private CountingCompletedTasksService countService;

  @Mock
  MentorsRepository mentorsRepository;

  @Mock
  StateMachineRepository stateMachineRepository;

  @BeforeAll
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(pointsTaskService, "abilityReviewMessage", "Ability message");
    ReflectionTestUtils.setField(pointsTaskService, "tasksForUsers",
            ("checkstyle, primitives, boxing, valueref, equals/hashcode,"
            + " patform, bytecode, gc, exceptions, classpath, generics,"
            + " inner/classes, override/overload, strings").split(","));
    ReflectionTestUtils.setField(pointsTaskService, "pointsForTask", ("1,2,2,3,3,"
            + "3,2,4,4,3,5,5,4,5").split(","));
  }


  @SneakyThrows
  @Test
  public void addPointForCompletedTaskTest() {
    List<String> pulls = new ArrayList<String>();
    pulls.add("checkstyle");
    pulls.add("primitives");
    pulls.add("boxing");

    HashMap<String, List<String>> result = new HashMap<>();
    result.put("marvintik", pulls);

    Mentors mentors = mock(Mentors.class);
    when(mentors.getGitNick()).thenReturn("test");
    StateEntity stateEntity =  new StateEntity();
    stateEntity.setUserID("Olena Haladzhii");
    stateEntity.setPointByTask(5);
    stateEntity.setGitName("marvintik");

    when(mentorsRepository.findByGitNick("test")).thenReturn(Optional.of(mentors));
    when(stateMachineRepository.findByGitName("marvintik")).thenReturn(Optional.of(stateEntity));
    when(countService.getCountedCompletedTasks()).thenReturn(result);

    pointsTaskService.addPointForCompletedTask("test", "marvintik", " valueref_test ");
    assertEquals(8, stateEntity.getPointByTask());
    assertEquals(3, pointsTaskService.checkPoints("valueref_test"));
  }

  @Test
  public void checkPointsTest() {
    String pullName = "valueref_test";
    String[] tasksForUsers =  ("checkstyle, primitives, boxing, valueref, "
            + "equals/hashcode, patform, bytecode, gc, exceptions, classpath,"
            + " generics, inner/classes, override/overload, strings").split(",");
    String[] pointsForTask = ("1,2,2,3,3,3,2,4,4,3,5,5,4,5").split(",");
    List<String> tasksList = Arrays.stream(tasksForUsers)
            .map(String::trim).collect(Collectors.toList());
    pullName = pullName.toLowerCase();
    int number = tasksList.stream().filter(pullName::contains)
            .map(tasksList::indexOf).findFirst().orElse(-1);
    System.out.println(number);
    assertEquals(3, Integer.parseInt(pointsForTask[number]));
  }


}