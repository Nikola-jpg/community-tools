package com.community.tools.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.community.tools.model.Mentors;
import com.community.tools.model.User;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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
    ExpressionParser parser = new SpelExpressionParser();
    EvaluationContext context = new StandardEvaluationContext();
    Map pointsForTask = (Map) parser.parseExpression("{'checkstyle':1, 'primitives':2,"
            + " 'boxing':2, 'valueref':3, 'equals/hashcode':3, 'patform':3,\n"
            + "  'bytecode':2, 'gc':4, 'exceptions':4, 'classpath':3, 'generics':5,"
            + " 'inner/classes':5, 'override/overload':4, 'strings':5}").getValue(context);
    ReflectionTestUtils.setField(pointsTaskService, "abilityReviewMessage", "Ability message");
    ReflectionTestUtils.setField(pointsTaskService, "pointsForTask", (pointsForTask));
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
    User stateEntity =  new User();
    stateEntity.setUserID("Olena Haladzhii");
    stateEntity.setPointByTask(5);
    stateEntity.setGitName("marvintik");

    when(mentorsRepository.findByGitNick("test")).thenReturn(Optional.of(mentors));
    when(stateMachineRepository.findByGitName("marvintik")).thenReturn(Optional.of(stateEntity));
    when(countService.getCountedCompletedTasks()).thenReturn(result);

    pointsTaskService.addPointForCompletedTask("test", "marvintik", " valueref_test ");
    assertEquals(8, stateEntity.getPointByTask());
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