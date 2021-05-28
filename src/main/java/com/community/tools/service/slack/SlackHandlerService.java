package com.community.tools.service.slack;

import com.community.tools.service.TrackingService;

import com.github.seratch.jslack.api.model.event.MessageEvent;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SlackHandlerService {

  @Value("${notThatMessage}")
  private String notThatMessage;
  @Value("${welcome}")
  private String welcome;
  @Value("${messageAboutRules}")
  private String messageAboutRules;
  @Value("${idOfSlackBot}")
  private String idOfSlackBot;
  @Value("${usersAgreeMessage}")
  private String usersAgreeMessage;
  @Value("${defaultMessage}")
  private String defaultMessage;
  @Value("${testModeSwitcher}")
  private Boolean testModeSwitcher;

  @Autowired
  private TrackingService trackingService;

  private TeamJoinHandler teamJoinHandler = new TeamJoinHandler() {
    @Override
    public void handle(TeamJoinPayload teamJoinPayload) {

      try {
        String userId = teamJoinPayload.getEvent().getUser().getId();
        trackingService.resetUser(userId);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  };

  private MessageHandler messageHandler = new MessageHandler() {
    @Override
    public void handle(MessagePayload teamJoinPayload) {
      MessageEvent messageEvent = teamJoinPayload.getEvent();
      if (!messageEvent.getUser().equals(idOfSlackBot)) {
        String messageFromUser = messageEvent.getText();
        String userId = messageEvent.getUser();
        try {
          if (messageFromUser.equals("reset") && testModeSwitcher) {
            trackingService.resetUser(userId);
          } else {
            trackingService.doAction(messageFromUser, userId);
          }
        } catch (Exception e) {
          throw new RuntimeException("Impossible to answer request with id="
              + teamJoinPayload.getEvent().getUser(), e);
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
      if (!testModeSwitcher) {
        dispatcher.register(teamJoinHandler);
      }
      dispatcher.register(messageHandler);
    }
  }

  @Bean
  public ServletRegistrationBean<GreatNewMemberServlet> servletRegistrationBean() {
    return new ServletRegistrationBean<>(new GreatNewMemberServlet(), "/greatNewMember/*");
  }
}