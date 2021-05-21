package com.community.tools.service;

import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;

public interface BlockService<T> {

  T nextTaskMessage(List<String> tasksList, int numberTask);

  T ratingMessage(String url, String img);

  T statisticMessage(StringBuilder messageBuilder, EmbedBuilder embedBuilder);

}
