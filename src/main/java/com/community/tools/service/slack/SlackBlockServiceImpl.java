package com.community.tools.service.slack;

import com.community.tools.service.BlockService;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("slack")
public class SlackBlockServiceImpl implements BlockService<String> {

  @Override
  public String nextTaskMessage(List<String> tasksList, int numberTask) {
    return MessagesToSlack.NEXT_TASK + tasksList.get(numberTask) + "|TASK>.\"}}]";
  }

  @Override
  public String ratingMessage(String url, String img) {
    return String.format(MessagesToSlack.LINK_PUBLISH_WEEK_STATS, url, img);
  }

  @Override
  public String statisticMessage(StringBuilder messageBuilder, EmbedBuilder embedBuilder) {
    return messageBuilder.toString();
  }
}
