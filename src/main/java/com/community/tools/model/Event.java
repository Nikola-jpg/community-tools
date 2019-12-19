package com.community.tools.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Event {

  private Date createdAt;
  private String actorLogin;
  private String type;
  private String state;
}
