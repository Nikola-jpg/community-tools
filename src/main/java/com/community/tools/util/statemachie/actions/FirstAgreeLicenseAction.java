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

public class FirstAgreeLicenseAction implements Action<State,Event> {

  @Value("${firstAgreeMessage}")
  private String firstAgreeMessage;
  @Autowired
  private SlackService slackService;

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    firstAgreeMessage = "ОСНОВНЫЕ ПРАВИЛА\n" +
            "1.Код форматируется в соответствии с Google code style.\n" +
            "Настройки для среды разработки (https://github.com/google/styleguide) :\n" +
            "IntelliJ IDEA https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml\n" +
            "Eclipse https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml\n" +
            "2.Все проекты собираются с помощью Maven.\n" +
            "3.Файлы среды разработки и прочие временные файлы не должны попадать в репозиторий (https://github.com/github/gitignore).\n" +
            "4.Покрытие кода Unit tests (Junit5):\n" +
            "Тест проверяет один кусок логики за раз. То есть, если необходимо проверить как работает метод, который мы проверяем с правильными данными – это один тест. Если необходимо проверить как работает метод с другими данными - второй тест.\n" +
            "Тест пишется по принципу:\n" +
            "*Подготовка тестовых данных.\n" +
            "*Исполнение метода, который мы тестируем.\n" +
            "*Проверка результата.";
    try {
      slackService.sendPrivateMessage(slackService.getUserById(user), firstAgreeMessage);
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
