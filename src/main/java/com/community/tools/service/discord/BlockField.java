package com.community.tools.service.discord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * To describe a block field.
 * @author Hryhorii Perets
 */
@Data
public class BlockField {
  private FieldName name;
  private String firstValue;
  private String secondValue;
  private String thirdValue;

  public BlockField() {
  }

  public BlockField(FieldName name) {
    this();
    this.name = name;
  }

  public BlockField(FieldName name, String firstValue) {
    this(name);
    this.firstValue = firstValue;
  }

  public BlockField(FieldName name, String firstValue, String secondValue) {
    this(name, firstValue);
    this.secondValue = secondValue;
  }

  public BlockField(FieldName name, String firstValue, String secondValue,
      String thirdValue) {
    this(name, firstValue, secondValue);
    this.thirdValue = thirdValue;
  }
}
