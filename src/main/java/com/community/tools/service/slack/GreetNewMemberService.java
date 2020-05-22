package com.community.tools.service.slack;

import static com.community.tools.util.statemachie.Event.ADD_GIT_NAME;
import static com.community.tools.util.statemachie.Event.GET_THE_FIRST_TASK;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;

import com.community.tools.service.github.GitHubService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.app_backend.events.EventsDispatcher;
import com.github.seratch.jslack.app_backend.events.handler.MessageHandler;
import com.github.seratch.jslack.app_backend.events.handler.TeamJoinHandler;
import com.github.seratch.jslack.app_backend.events.payload.MessagePayload;
import com.github.seratch.jslack.app_backend.events.payload.TeamJoinPayload;
import com.github.seratch.jslack.app_backend.events.servlet.SlackEventsApiServlet;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GreetNewMemberService {

  @Value("${DB_URL}")
  private String dbUrl;
  @Value("${DB_USER_NAME}")
  private String username;
  @Value("${DB_PASSWORD}")
  private String password;

  @Value("${welcome}")
  private String welcome;
  @Value("${checkNickName}")
  private String checkNickName;
  @Value("${congratsAvailableNick}")
  private String congratsAvailableNick;
  @Value("${getFirstTask}")
  private String getFirstTask;
  @Value("${failedCheckNickName}")
  private String failedCheckNickName;
  @Value("${doNotUnderstandWhatTodo}")
  private String doNotUnderstandWhatTodo;
  @Value("${agreeMessage}")
  private String agreeMessage;

  private final SlackService slackService;
  private final GitHubService gitHubService;
  @Autowired
  private StateMachineFactory<State, Event> factory;
  @Autowired
  private StateMachinePersister<State, Event, String> persister;


  private TeamJoinHandler teamJoinHandler = new TeamJoinHandler() {
    @Override
    public void handle(TeamJoinPayload teamJoinPayload) {

      try {
        StateMachine<State, Event> machine = factory.getStateMachine();
        machine.start();
        String user = teamJoinPayload.getEvent().getUser().getId();
        persister.persist(machine, user);
        slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser().getRealName(),
            welcome);
        slackService
            .sendBlocksMessage(teamJoinPayload.getEvent().getUser().getRealName(), agreeMessage);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  };

  private MessageHandler messageHandler = new MessageHandler() {
    @Override
    public void handle(MessagePayload teamJoinPayload) {
      if (!teamJoinPayload.getEvent().getUser().equals("UQWD538CT")) {
        try {
          String userId = teamJoinPayload.getEvent().getUser();
          String user = slackService.getUserById(userId);

          StateMachine<State, Event> machine = factory.getStateMachine();
          machine.start();
          persister.restore(machine, userId);

          if (machine.getState().getId() == AGREED_LICENSE) {
            String nickName = teamJoinPayload.getEvent().getText();
            slackService.sendPrivateMessage(user,
                checkNickName + nickName);

            boolean nicknameMatch = gitHubService.getGitHubAllUsers().stream()
                .anyMatch(e -> e.getLogin().equals(nickName));
            if (nicknameMatch) {
              SingleConnectionDataSource connect = new SingleConnectionDataSource();
              connect.setDriverClassName("org.postgresql.Driver");
              connect.setUrl(dbUrl);
              connect.setUsername(username);
              connect.setPassword(password);
              JdbcTemplate jdbcTemplate = new JdbcTemplate(connect);
              jdbcTemplate.update("UPDATE public.state_entity SET  git_name= '" + nickName + "'"
                  + "\tWHERE userid='" + userId + "';");

              slackService.sendPrivateMessage(user, congratsAvailableNick);
              machine.sendEvent(ADD_GIT_NAME);

              slackService.sendBlocksMessage(user, getFirstTask);
              machine.sendEvent(GET_THE_FIRST_TASK);
              persister.persist(machine, userId);

            } else {
              slackService.sendPrivateMessage(user, failedCheckNickName);
            }

          } else {
            slackService.sendPrivateMessage(user, doNotUnderstandWhatTodo);

          }

        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
  };

  public class GreatNewMemberServlet extends SlackEventsApiServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      super.doPost(req, resp);
    }

    @Override
    protected void setupDispatcher(EventsDispatcher dispatcher) {
      dispatcher.register(teamJoinHandler);
      dispatcher.register(messageHandler);
    }
  }

  @Bean
  public ServletRegistrationBean<GreatNewMemberServlet> servletRegistrationBean() {
    return new ServletRegistrationBean<>(new GreatNewMemberServlet(), "/greatNewMember/*");
  }
}
