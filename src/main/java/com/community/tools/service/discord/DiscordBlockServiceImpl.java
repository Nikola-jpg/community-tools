package com.community.tools.service.discord;

import com.community.tools.model.Messages;
import com.community.tools.service.BlockService;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("discord")
public class DiscordBlockServiceImpl implements BlockService<MessageEmbed> {

  @Override
  public MessageEmbed nextTaskMessage(List<String> tasksList, int numberTask) {
    return new EmbedBuilder()
        .addField("", MessagesToDiscord.NEXT_TASK + tasksList.get(numberTask) + ") :link:", false)
        .build();
  }

  @Override
  public MessageEmbed ratingMessage(String url, String img) {
    return  new EmbedBuilder()
        .addField("","Рейтинг этой недели доступен по ссылке: ", false)
        .addField("","[:loudspeaker: click_me_123](" + url + ")", false)
        .addField("","[Image](" + img + ")", true)
        .build();
  }

  @Override
  public MessageEmbed statisticMessage(StringBuilder messageBuilder, EmbedBuilder embedBuilder) {
    return embedBuilder.build();
  }
}
