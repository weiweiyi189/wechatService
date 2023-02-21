package com.yunzhi.wechatService.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;


    private String client;

    @Column(nullable = false, length = 200)
    private String username;

    public User(String username, String client) {
        this.username = username;
        this.client = client;
    }

    @ManyToOne()
    WeChatUser weChatUser;

    public User() {}

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
    @JsonBackReference
    public WeChatUser getWeChatUser() {
        return weChatUser;
    }

    public void setWeChatUser(WeChatUser weChatUser) {
        this.weChatUser = weChatUser;
    }
}
