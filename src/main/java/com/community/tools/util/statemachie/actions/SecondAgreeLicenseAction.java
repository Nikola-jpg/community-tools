package com.community.tools.util.statemachie.actions;

import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class SecondAgreeLicenseAction implements Action<State, Event> {

  @Value("${secondAgreeMessage}")
  private String secondAgreeMessage;
  @Autowired
  private SlackService slackService;

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    secondAgreeMessage ="ПРАВИЛА ВЫПОЛНЕНИЯ ЗАДАНИЯ\n" +
            "Первое, что тебе нужно сделать - Fork репозиторий. Если ты форкнул репозиторий не только что, а уже давно работаешь над заданиями – тебе нужно обновиться. Инструкция как это сделать:\n" +
            "https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/merging-an-upstream-repository-into-your-fork\n" +
            "Если ты дисциплинированно создавал ветки и не добавлял коммиты в master то все должно пройти без конфликтов =)\n" +
            "Каждое новое задание необходимо выполнять по следующему алгоритму:\n" +
            "\n" +
            "Каждое задание – отдельная ветка. Сделай ветку для задания и выполняй его в соответствии с инструкциями. Имя ветки должно совпадать с именем пакета задания.\n" +
            "Сделай pull request из этой ветки в наш репозиторий. Имя pull request должно совпадать с именем пакета задания.\n" +
            "Мы проверим задание, подскажем что нужно исправить и примем задание. Если что-то надо поменять мы добавим метку changes requested. Если это произошло:\n" +
            "2.1. Исправь, пожалуйста, замечания.\n" +
            "2.2. Удали метку changes requested и добавь метку ready for review.\n" +
            "2.3. Метка done – задание принято.\n" +
            "2.4. Если ты не можешь менять метки – значит мы провтыкали и не добавили тебя в команду (или добавили, но приглашение не было принято, так что проверь почту) – напиши об этом Anton Chernetskiy в слеке.\n" +
            "\n" +
            "Yay, you've read all the requirements! Congratulations! Below is a link to your first task. Good luck :) + ссылка на первое задание.";
    try {
      slackService.sendPrivateMessage(slackService.getUserById(user), secondAgreeMessage);
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
