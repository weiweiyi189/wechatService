package com.yunzhi.wechatService.service;


import com.yunzhi.wechatService.entity.WeChatUser;

public interface WechatService {

  WeChatUser getOneByOpenidAndAppId(String openId, String toUser);
}
