package com.community.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import org.junit.jupiter.api.Test;

public class AddPointsTest {


  @SneakyThrows
  @Test
  public void addPointIfPullLabeledDoneTest() {
    InputStream is = new FileInputStream("src/test/resources/label.json");
    String jsonTxt = IOUtils.toString(is, "UTF-8");
    JSONObject json = new JSONObject(jsonTxt);
    List<Object> list = json.getJSONObject("pull_request").getJSONArray("labels").toList();
    boolean containDone = list.stream().map(o -> (HashMap) o)
            .anyMatch(e -> e.get("name").equals("done"));
    assertTrue(containDone);

    String sender = json.getJSONObject("sender").getString("login");
    String creator = json.getJSONObject("pull_request").getJSONObject("user").getString("login");
    String pullName = json.getJSONObject("pull_request").getString("title");

    assertEquals("test", sender);
    assertEquals("test", creator);
    assertEquals("inner/classes", pullName);
    assertEquals("labeled", json.get("action"));
    assertEquals("done", json.getJSONObject("label").getString("name"));

  }
}
