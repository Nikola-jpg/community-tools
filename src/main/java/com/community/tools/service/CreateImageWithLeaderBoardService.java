package com.community.tools.service;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JEditorPane;

import lombok.SneakyThrows;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CreateImageWithLeaderBoardService {

  /**
   * This method put html code into JEditorPane and print image.
   * @param url url with endpoint leaderboard
   * @return byte array with image
   */
  @SneakyThrows
  public byte[] createImage(String url) {
    String html = getSourceCodeFromSite(url);
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
   * This method read the source code of html page and put it into StringBuilder.
   * @param url url with endpoint leaderboard
   */
  public String getSourceCodeFromSite(String url) {
    String generateUrl = url + "better/";
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> responseEntity =
            restTemplate.exchange(generateUrl, HttpMethod.GET, null, String.class);
    return responseEntity.getBody();
  }

}
