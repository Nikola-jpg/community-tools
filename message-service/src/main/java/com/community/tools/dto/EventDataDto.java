package com.community.tools.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDataDto {

  private String createdAt;
  private String actorLogin;
  private Map<String,String> type;

}
