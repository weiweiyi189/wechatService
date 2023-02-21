package com.yunzhi.wechatService.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 微信用户
 */
@Entity
public class WeChatUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String openid;

  @Column(nullable = false)
  private String appId;

  @OneToMany(mappedBy = "weChatUser")
  private List<User> users;

  public WeChatUser() {
  }

  public WeChatUser(String openid, String appId) {
    this.openid = openid;
    this.appId = appId;
  }

  public void setOpenid(String openid) {
    this.openid = openid;
  }

  public String getOpenid() {
    return this.openid;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }


  @JsonManagedReference
  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }
}
