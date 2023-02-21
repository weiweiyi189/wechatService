package com.yunzhi.wechatService.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * http响应实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpClientResponse {
  private int httpCode;
  private String responseBody;
}