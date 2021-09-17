package com.community.tools.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventData {

  private Date createdAt;
  private String actorLogin;
  private Event type;

}
