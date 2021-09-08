package com.community.tools.dto;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventDataTransformerTest {

  @Test
  public void shouldConvertDtoIntoEntity() {
    //given
    Date date = new Date();
    EventDataDto dto = new EventDataDto(date.toString(), "actorLogin",
        Collections.singletonMap(
        "COMMENT",
        "Comment"
    ));
    //when
    EventData actualEntity = EventDataTransformer.convertToEntity(dto);
    //then
    Assert.assertEquals("actorLogin", actualEntity.getActorLogin());
    Assert.assertEquals(Event.COMMENT, actualEntity.getType());
    Assert.assertEquals(date.toString(), actualEntity.getCreatedAt().toString());
  }

  @Test
  public void shouldConvertEntityIntoDto() {
    //given
    Date date = new Date();
    EventData expectedEntity = new EventData(date, "actorLogin", Event.COMMIT);
    Map<String, String> event = Collections.singletonMap("COMMIT", "Commit");
    //when
    EventDataDto actualDto = EventDataTransformer.convertToDto(expectedEntity);
    //then
    Assert.assertEquals("actorLogin", actualDto.getActorLogin());
    Assert.assertEquals(date.toString(), actualDto.getCreatedAt());
    Assert.assertEquals(event, actualDto.getType());
  }

}
