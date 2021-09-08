package com.community.tools.dto;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;
import lombok.SneakyThrows;

public class EventDataTransformer {

  /**
   * Convert an EventData object into an EventDataDto object.
   * @param entity input entity.
   * @return a new EventDataDto object.
   */
  public static EventDataDto convertToDto(EventData entity) {
    EventDataDto dto = new EventDataDto();
    dto.setActorLogin(entity.getActorLogin());
    dto.setType(Collections.singletonMap(
        entity.getType().name(),
        entity.getType().getTitle()
    ));
    SimpleDateFormat formatter = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
        Locale.ENGLISH);
    dto.setCreatedAt(formatter.format(entity.getCreatedAt()));

    return dto;
  }

  /**
   * Convert an EventDataDto object into an EventData object.
   * @param dto input dto.
   * @return a new EventData object.
   */
  @SneakyThrows
  public static EventData convertToEntity(EventDataDto dto) {
    EventData entity = new EventData();
    entity.setActorLogin(dto.getActorLogin());

    SimpleDateFormat formatter = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
        Locale.ENGLISH);
    entity.setCreatedAt(formatter.parse(dto.getCreatedAt()));

    String type = (String) dto.getType().keySet().toArray()[0];
    entity.setType(Event.valueOf(type));
    return entity;
  }
}
