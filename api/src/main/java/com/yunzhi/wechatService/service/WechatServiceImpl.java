package com.yunzhi.wechatService.service;

import com.yunzhi.wechatService.entity.User;
import com.yunzhi.wechatService.entity.WeChatUser;
import com.yunzhi.wechatService.repository.UserRepository;
import com.yunzhi.wechatService.repository.WeChatUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class WechatServiceImpl implements WechatService {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final WeChatUserRepository weChatUserRepository;


  private final UserRepository userRepository;


  public WechatServiceImpl(WeChatUserRepository weChatUserRepository,
                           UserRepository userRepository) {
    this.weChatUserRepository = weChatUserRepository;
    this.userRepository = userRepository;
  }

  @Override
  public WeChatUser getOneByOpenidAndAppId(String openId, String appId) {
    return this.weChatUserRepository.findByOpenidAndAppId(openId, appId)
        .orElseGet(() -> this.weChatUserRepository.save(new WeChatUser(openId, appId)));
  }
}
