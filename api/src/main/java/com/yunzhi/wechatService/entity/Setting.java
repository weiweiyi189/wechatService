package com.yunzhi.wechatService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Setting {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String url = "";

  @Column(nullable = false, unique = true, length = 150)
  private String client = "";

  @Column(nullable = false)
  private String name;
}
