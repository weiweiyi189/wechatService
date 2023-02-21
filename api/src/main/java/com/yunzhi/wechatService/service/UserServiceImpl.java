package com.yunzhi.wechatService.service;


import com.yunzhi.wechatService.entity.ExpiredMap;
import com.yunzhi.wechatService.entity.Setting;
import com.yunzhi.wechatService.entity.User;
import com.yunzhi.wechatService.entity.WeChatUser;
import com.yunzhi.wechatService.repository.SettingRepository;
import com.yunzhi.wechatService.repository.UserRepository;
import com.yunzhi.wechatService.repository.WeChatUserRepository;
import com.yunzhi.wechatService.wxmessagebuilder.TextBuilder;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final UserRepository userRepository;

  private final WeChatMpService wxMpService;
  private final WeChatUserRepository weChatUserRepository;

  private final WechatService wechatService;

  private final SettingRepository settingRepository;

  private final ExpiredMap<String, String> map = new ExpiredMap<>();


  public UserServiceImpl(UserRepository userRepository,
                         WeChatMpService wxMpService,
                         SettingRepository settingRepository,
                         WeChatUserRepository weChatUserRepository,
                         WechatService wechatService) {
    this.userRepository = userRepository;
    this.wxMpService = wxMpService;
    this.weChatUserRepository = weChatUserRepository;
    this.wechatService = wechatService;
    this.settingRepository = settingRepository;
  }

  @Transactional
  void bindWeChatUserToUser(WeChatUser weChatUser, String username, String clientName) {
    WeChatUser wechat = this.weChatUserRepository.findById(weChatUser.getId()).get();
    User user = this.userRepository.findByUsernameAndClient(username, clientName)
            .orElseGet(() -> this.userRepository.save(new User(username, clientName)));
    user.setWeChatUser(wechat);
    this.userRepository.save(user);
  }

  User getUserByWeChatUserAndClient(WeChatUser weChatUser, String clientName) {
    if (weChatUser.getUsers().isEmpty()) {
      return null;
    }
    // 查看该微信用户是否绑定了该用户
    for (User user : weChatUser.getUsers()) {
      if (Objects.equals(user.getClient(), clientName)) {
        return user;
      }
    }
    return null;
  }


  /**
   * 生成与用户绑定的二维码
   *
   * @param sessionId sessionId
   * @param client
   * @return 扫描后触发的回调关键字
   */
  @Override
  public String generateBindQrCode(String sessionId, String username, String client) {
    try {
      if (this.logger.isDebugEnabled()) {
        this.logger.info("1. 生成用于回调的sceneStr，请将推送给微信，微信当推送带有sceneStr的二维码，用户扫码后微信则会把带有sceneStr的信息回推过来");
      }
      // 生成临时二维码场景值，之后微信回调信息会回发该值，根据此值调用handler
      // 例如ScanHandler的handleKey函数， 那里的wxMpXmlMessage.getEventKey()的值，就是该场景值
      String sceneStr = UUID.randomUUID().toString();
      WxMpQrCodeTicket wxMpQrCodeTicket = this.wxMpService.getQrcodeService().qrCodeCreateTmpTicket(sceneStr, 10 * 60);

      Setting setting = this.settingRepository.findByClient(client).orElseThrow(() -> new Error("未找到client为" + client + "的实体"));

      this.wxMpService.addHandler(sceneStr, new WeChatMpEventKeyHandler() {
        long beginTime = System.currentTimeMillis();
        private Logger logger = LoggerFactory.getLogger(this.getClass());

        @Override
        public boolean getExpired() {
          return System.currentTimeMillis() - beginTime > 10 * 60 * 1000;
        }

        @Override
        public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, WeChatUser weChatUser) {

          // 微信用户的id
          String openid = wxMpXmlMessage.getFromUser();
          if (openid == null) {
            this.logger.error("openid is null");
          }

          final Map<String, String> variables = new HashMap<>();
          variables.put("username", username);
          variables.put("sessionId", sessionId);
          variables.put("openId", openid);

          String url = setting.getUrl() + "/api/wechat/bindUser";

          CommonServiceImpl.httpPost(url, weChatUser, variables);
          bindWeChatUserToUser(weChatUser, username, client);

          return new TextBuilder().build(String.format("您当前的微信号已与 %s 系统用户 %s 成功绑定。", setting.getName(),  username),
              wxMpXmlMessage,
              null);
        }
      });
      return this.wxMpService.getQrcodeService().qrCodePictureUrl(wxMpQrCodeTicket.getTicket());
    } catch (Exception e) {
      this.logger.error("获取临时公众号图片时发生错误：" + e.getMessage());
    }
    return "";
  }


  /**
   * 校验微信扫码登录后的认证ID是否有效
   * @param wsAuthUuid websocket认证ID
   */
  @Override
  public boolean checkWeChatLoginUuidIsValid(String wsAuthUuid) {
    return this.map.containsKey(wsAuthUuid);
  }


  @Override
  public String getLoginQrCode(String wsLoginToken, String clientName) {
    try {
      if (this.logger.isDebugEnabled()) {
        this.logger.info("1. 生成用于回调的sceneStr，请将推送给微信，微信当推送带有sceneStr的二维码，用户扫码后微信则会把带有sceneStr的信息回推过来");
      }
      // 生成临时二维码场景值，之后微信回调信息会回发该值，根据此值调用handler
      // 例如ScanHandler的handleKey函数， 那里的wxMpXmlMessage.getEventKey()的值，就是该场景值
      String sceneStr = UUID.randomUUID().toString();
      WxMpQrCodeTicket wxMpQrCodeTicket = this.wxMpService.getQrcodeService().qrCodeCreateTmpTicket(sceneStr, 10 * 60);
      Setting setting = this.settingRepository.findByClient(clientName).orElseThrow(() -> new Error("未找到client为" + clientName + "的实体"));

        this.wxMpService.addHandler(sceneStr, new WeChatMpEventKeyHandler() {
        long beginTime = System.currentTimeMillis();
        private Logger logger = LoggerFactory.getLogger(this.getClass());

        @Override
        public boolean getExpired() {
          return System.currentTimeMillis() - beginTime > 10 * 60 * 1000;
        }

        /**
         * 扫码后调用该方法
         * @param wxMpXmlMessage 扫码消息
         * @param weChatUser 扫码用户
         * @return 输出消息
         */
        @Override
        public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, WeChatUser weChatUser) {

          // 微信用户的id
          String openid = wxMpXmlMessage.getFromUser();
          if (openid == null) {
            this.logger.error("openid is null");
          }
          User user = getUserByWeChatUserAndClient(weChatUser, clientName);
          if (user != null) {
            final Map<String, String> variables = new HashMap<>();
            variables.put("wsLoginToken", wsLoginToken);
            variables.put("username", user.getUsername());

            String url = setting.getUrl() + "/api/wechat/login";

            CommonServiceImpl.httpPost(url, weChatUser, variables);
            bindWeChatUserToUser(weChatUser, user.getUsername(), clientName);
            return new TextBuilder().build(String.format("登录客户端： %s成功，登录的用户为： %s", setting.getName(), user.getUsername()),
                wxMpXmlMessage,
                null);
          } else {
            return new TextBuilder().build(String.format("登录客户端： %s 失败，原因：您尚未绑定微信用户", setting.getName()),
                wxMpXmlMessage,
                null);
          }
        }
      });
      return this.wxMpService.getQrcodeService().qrCodePictureUrl(wxMpQrCodeTicket.getTicket());
    } catch (Exception e) {
      this.logger.error("获取临时公众号图片时发生错误：" + e.getMessage());
    }
    return "";
  }
}
