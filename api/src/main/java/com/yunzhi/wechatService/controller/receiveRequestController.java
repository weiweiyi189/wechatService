package com.yunzhi.wechatService.controller;

import com.yunzhi.wechatService.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping("request")
@RestController
public class receiveRequestController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public receiveRequestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("getBindQrCode")
    public String getQrCode(@RequestParam String sessionId,
                            @RequestParam String username,
                            @RequestParam String client) {
        return this.userService.generateBindQrCode(sessionId, username, client);
    }

    /**
     * 获取登录的二维码
     * @param wsLoginToken webSocket认证token
     * @param client 客户端
     * @return 二维码对应的系统ID(用于触发扫码后的回调)
     */
    @GetMapping("getLoginQrCode")
    public String getLoginQrCode(
            @RequestParam String wsLoginToken,
            @RequestParam String client) {
        return this.userService.getLoginQrCode(wsLoginToken, client);
    }
}
