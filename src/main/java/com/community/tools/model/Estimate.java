package com.community.tools.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Estimate {

  @Id
  @GeneratedValue
  private Integer id;

  private String name;


}
