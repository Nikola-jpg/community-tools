package com.community.tools.service.slack;

import static com.community.tools.util.statemachie.Event.AGREE_LICENSE;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;

import com.community.tools.service.StateMachineService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.github.seratch.jslack.app_backend.events.EventsDispatcher;
import com.github.seratch.jslack.app_backend.events.handler.MessageHandler;
import com.github.seratch.jslack.app_backend.events.handler.TeamJoinHandler;
import com.github.seratch.jslack.app_backend.events.payload.MessagePayload;
import com.github.seratch.jslack.app_backend.events.payload.TeamJoinPayload;
import com.github.seratch.jslack.app_backend.events.servlet.SlackEventsApiServlet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class GreetNewMemberService {

  @Value("${notThatMessage}")
  private String notThatMessage;
  @Value("${welcome}")
  private String welcome;
  @Value("${idOfSlackBot}")
  private String idOfSlackBot;
  @Value("${agreeMessage}")
  private String agreeMessage;
  @Value("${usersAgreeMessage}")
  private String usersAgreeMessage;

  private final SlackService slackService;
  private final StateMachineService stateMachineService;
  @Autowired
  private StateMachineRepository stateMachineRepository;

  private TeamJoinHandler teamJoinHandler = new TeamJoinHandler() {
    @Override
    public void handle(TeamJoinPayload teamJoinPayload) {
      agreeMessage = "Мы собрались здесь, чтобы стать крутыми комерческими разработчикамиblush\n Для начала разберемся с определениями:\n Кто такой комерческий разработчик? – Это человек, который приносит бизнесу деньги.\n Как он может это делать? – Разрабатывать софт, который зарабатывает или экономит деньги. Для этого он должен, затратив минимальное количество ресурсов, разработать софт, имеющий внутреннее и внешнее качество.\n Внешнее качество – на сколько хорошо софт решает бизнес задачу.\n Внутреннее качество – на сколько легко созданный софт развивать, поддерживать, а также как легко его понимать другим членам команды.\n Какими навыками обладает крутой разработчик? – Кроме технических навыков, крутой разработчик обладает «soft skills». В первую очередь это умение помогать членам команды. Работая в команде, мы можем приумножить результаты своих усилий, научив людей тому, что умеем, и учась у них. Важной частью этого навыка есть умение критиковать конструктивно. Мы не говорим, что сделано плохо, а говорим, что можно сделать лучше и почему!\n Итого, наши принципы:\n \n Цель работы разработчика – за минимальное время сделать максимально качественное ПО, мы хотим совершенствовать этот навык.\n Взаимопомощь – мы работаем в команде и помогаем друг другу.\n Конструктивная критика – мы говорим, что можно сделать лучше, а не что сделано плохо.\n Сообщение для диалога со стажером:\n If you agree with us, write \"I agree\". Follow this action until you receive the following instructions:)";

      try {
        String user = teamJoinPayload.getEvent().getUser().getId();
        StateEntity stateEntity = new StateEntity();
        stateEntity.setUserID(user);
        stateMachineRepository.save(stateEntity);

        stateMachineService.persistMachineForNewUser(user);
        slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser().getRealName(),
            welcome);
        slackService
            .sendPrivateMessage(teamJoinPayload.getEvent().getUser().getRealName(), agreeMessage);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  };

  private MessageHandler messageHandler = new MessageHandler() {
    @Override
    public void handle(MessagePayload teamJoinPayload) {
      if (!teamJoinPayload.getEvent().getUser().equals(idOfSlackBot)) {
        try {
          StateMachine<State, Event> machine = stateMachineService
              .restoreMachine(teamJoinPayload.getEvent().getUser());
          switch (machine.getState().getId()) {
            case AGREED_LICENSE:
              machine.getExtendedState().getVariables()
                  .put("gitNick", teamJoinPayload.getEvent().getText());

              machine.sendEvent(Event.ADD_GIT_NAME);
              machine.sendEvent(Event.GET_THE_FIRST_TASK);
              stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              break;
            case NEW_USER:
              if (teamJoinPayload.getEvent().getText().equals(usersAgreeMessage)){
                machine.sendEvent(Event.FIRST_AGREE_MESS);
                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              }else{
                slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser(), notThatMessage);
              }
              break;
            case FIRST_LICENSE_MESS:
              if (teamJoinPayload.getEvent().getText().equals(usersAgreeMessage)){
                machine.sendEvent(Event.SECOND_AGREE_MESS);
                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              }else{
                slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser(), notThatMessage);
              }
              break;
            case SECOND_LICENSE_MESS:
              if (teamJoinPayload.getEvent().getText().equals(usersAgreeMessage)){
                machine.sendEvent(AGREE_LICENSE);
                stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
              }else{
                slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser(), notThatMessage);
              }
              break;

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
