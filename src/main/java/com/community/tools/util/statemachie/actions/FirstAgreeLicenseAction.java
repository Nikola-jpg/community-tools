package com.community.tools.util.statemachie.actions;

import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;

import com.google.gson.JsonParseException;
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
    firstAgreeMessage = "[\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"header\",\n" +
            "\t\t\t\"text\": {\n" +
            "\t\t\t\t\"type\": \"plain_text\",\n" +
            "\t\t\t\t\"text\": \"ОСНОВНЫЕ ПРАВИЛА\",\n" +
            "\t\t\t\t\"emoji\": true\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"divider\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"context\",\n" +
            "\t\t\t\"elements\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"type\": \"mrkdwn\",\n" +
            "\t\t\t\t\t\"text\": \" 1.   Код форматируется в соответствии с *Google code style*. \\nНастройки для среды разработки (https://github.com/google/styleguide): \\n <https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml|IntelliJ IDEA> \\n <https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml|Eclipse>\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"context\",\n" +
            "\t\t\t\"elements\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"type\": \"mrkdwn\",\n" +
            "\t\t\t\t\t\"text\": \"  2.   Все проекты собираются с помощью *Maven*.\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"context\",\n" +
            "\t\t\t\"elements\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"type\": \"mrkdwn\",\n" +
            "\t\t\t\t\t\"text\": \"3. Файлы среды разработки и прочие временные файлы не должны попадать в репозиторий (https://github.com/github/gitignore).\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"context\",\n" +
            "\t\t\t\"elements\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"type\": \"mrkdwn\",\n" +
            "\t\t\t\t\t\"text\": \"4.   Покрытие кода *Unit tests* (Junit5): \\n\\tТест проверяет один кусок логики за раз. То есть, если необходимо проверить как работает метод, который мы проверяем с правильными данными – это _один тест_. Если необходимо проверить как работает метод с другими данными - _второй тест_. \\n\\n *Тест пишется по принципу:* \\n\\t 1.  Подготовка тестовых данных. \\n\\t 2.  Исполнение метода, который мы тестируем. \\n\\t 3.  Проверка результата.\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"context\",\n" +
            "\t\t\t\"elements\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"type\": \"mrkdwn\",\n" +
            "\t\t\t\t\t\"text\": \"`If you agree, enter \\\"I agree\\\" to continue.`\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t}\n" +
            "\t]";
    try {
      slackService.sendBlocksMessage(slackService.getUserById(user), firstAgreeMessage);
    }catch (JsonParseException e){
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
