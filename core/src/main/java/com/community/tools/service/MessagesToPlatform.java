package com.community.tools.service;

import com.community.tools.model.EventData;
import java.util.List;

public abstract class MessagesToPlatform<T> {

  //messages for bot
  public T getFirstTask;
  public T addGitName;
  public T noOneCase;
  public T notThatMessage;
  public T abilityReviewMessage;
  public T firstQuestion;
  public T secondQuestion;
  public T thirdQuestion;
  public T messageAboutRules;
  public T errorWithAddingGitName;
  public T estimateTheTask;

  public abstract T failedBuildMessage(String url, String task);

  public abstract T infoLinkMessage(String info, String url, String img);

  public abstract T statisticMessage(List<EventData> events);

  public abstract T nextTaskMessage(List<String> tasksList, int numberTask);

  //Information channels message
  public T messageAboutSeveralInfoChannel;

}
