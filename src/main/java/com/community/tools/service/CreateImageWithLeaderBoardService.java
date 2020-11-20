package com.community.tools.service;

import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.JEditorPane;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class CreateImageWithLeaderBoardService {

  @Autowired
  StateMachineRepository stateMachineRepository;

  @Autowired
  TemplateEngine templateEngine;

  /**
   * This method put html code into JEditorPane and print image.
   * @param url url with endpoint leaderboard
   * @return byte array with image
   */
  @SneakyThrows
  public byte[] createImage(String url) {
    String html = getLeaderboardTemplate();
    int width = 550;
    int height = 225;

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics graphics = image.createGraphics();

    JEditorPane jep = new JEditorPane("text/html", html);
    jep.setSize(width, height);
    jep.print(graphics);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ImageIO.write(image, "png", bos);
    byte[] data = bos.toByteArray();
    return data;
  }

  /**
   * This method return html-content with table, which contains first 5 trainees of leaderboard.
   * @return HtmlContent with leaderboard image
   */
  public String getLeaderboardTemplate() {
    final Context ctx = new Context();
    List<StateEntity> list = stateMachineRepository.findAll();
    list.sort(Comparator.comparing(StateEntity::getTotalPoints).reversed());
    List<StateEntity> listFirst = list.stream().limit(5).collect(Collectors.toList());
    ctx.setVariable("entities", listFirst);
    final String htmlContent = this.templateEngine.process("leaderboard.html", ctx);
    return  htmlContent;
  }

}
