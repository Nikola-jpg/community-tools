package com.community.tools.util;

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

public class CreateImageWithLeaderBoard {

  private static StringBuilder sb = new StringBuilder();

  /**
   * This method put html code into JEditorPane and print image.
   * @param url url with endpoint leaderboard
   * @return byte array with image
   */
  @SneakyThrows
  public static byte[] createImage(String url) {
    getSourceCodeFromSite(url);
    String html = sb.toString();
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
  public static void getSourceCodeFromSite(String url) {
    String generateUrl = url + "better/";
    String inputLine;
    try {
      URL data = new URL(generateUrl);

      HttpURLConnection con = (HttpURLConnection) data.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      while ((inputLine = in.readLine()) != null) {
        sb.append(inputLine);
      }
      in.close();
      con.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
