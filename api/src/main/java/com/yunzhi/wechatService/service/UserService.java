package com.yunzhi.wechatService.service;

import javax.servlet.http.HttpSession;


public interface UserService {

  /**
   * 获取获取的二维码
   * @param wsLoginToken 用于登录的wsLoginToken
   * @param clientName clientName
   * @return 二维码图片地址
   */
  String getLoginQrCode(String wsLoginToken, String clientName);

  /**
   * 生成绑定当前用户的微信二维码
   *
   * @param sessionId sessionId
   * @param client
   * @return 返回图片URL地址
   */
  String generateBindQrCode(String sessionId, String username, String client);


  boolean checkWeChatLoginUuidIsValid(String uuid);


}
