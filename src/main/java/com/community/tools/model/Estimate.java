package com.community.tools.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "estimate")
public class Estimate {

  @Id
  @GeneratedValue
  private Integer id;

  private String name;


}
