package com.yunzhi.wechatService.service;

import com.yunzhi.wechatService.config.HttpsClientRequestFactory;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.EOFException;
import java.util.*;

@Service
public class CommonServiceImpl {

  @Retryable(value = {RestClientException.class, EOFException.class}, maxAttempts = 3,
          backoff = @Backoff(delay = 1000L,multiplier = 1))
  static public String httGet(String url, Map<String, String> variables) {

    // 添加参数格式
    String requestUrl = CommonServiceImpl.addParam(url, variables);

    System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");

    RestTemplate restTemplate = new RestTemplate(new HttpsClientRequestFactory());
    String response = restTemplate.getForObject(requestUrl, String.class, variables);
    return response;
  }

  @Retryable(value = {RestClientException.class, EOFException.class}, maxAttempts = 3,
          backoff = @Backoff(delay = 1000L,multiplier = 1))
  static public <T> void httpPost(String url , T entity, Map<String, String> variables) {

    //创建请求头
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    // 添加url参数格式
    String requestUrl = CommonServiceImpl.addParam(url, variables);

    HttpEntity<T> requestEntity = new HttpEntity<T>(entity, headers);
    RestTemplate restTemplate = new RestTemplate();
    // 发送post请求
    ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, String.class, variables);

    // 判断请求是否发生异常
    if(!response.getStatusCode().is2xxSuccessful()){
      System.out.println("请求失败...");
      // 抛出异常
      throw new RestClientException(response.getBody());
    }
  }


  static public String addParam(String url , Map<String, String> variables) {
    String requestUrl = url;
    String symbol = "?";

    // 添加url参数格式
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      requestUrl = requestUrl + symbol + entry.getKey() + "={" + entry.getKey() + "}";
      symbol = "&";
    }

    return requestUrl;
  }
}
