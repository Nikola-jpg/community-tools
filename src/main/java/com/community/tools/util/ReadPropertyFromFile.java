package com.community.tools.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.SneakyThrows;

public class ReadPropertyFromFile {


  /**
   * This method reads text in the file and finds properties that are separated by "=".
   * @param fileName file, which contains properties
   * @return HashMap  Key(String) - name of property, Value(String) - value of property
   */
  @SneakyThrows
  public static HashMap<String, String> readPropertiesFromFile(String fileName) {
    HashMap<String,String> property = new HashMap<>();
    Path pathProp = Paths.get(fileName);
    Stream<String> linesProp = Files.lines(pathProp);
    List<String> propList = linesProp.collect(Collectors.toList());
    for (String str : propList) {
      String[] arr = str.split(" = ");
      property.put(arr[0], arr[1]);
    }
    linesProp.close();
    return property;
  }
}
