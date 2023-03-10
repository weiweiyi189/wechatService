package com.yunzhi.wechatService.util;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * httpClient装饰器
 */
public class HttpClientWrapper {
  private final CloseableHttpClient httpClient;
  public HttpClientWrapper(CloseableHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public HttpClientResponse postReturnHttpResponse(Map<String, String> headerMap, String url, String bodyStr) throws IOException {
    HttpPost httpPost = getHttpPost(headerMap, url, bodyStr);

    return convert2HttpClientResponse(httpClient.execute(httpPost));
  }

  private HttpPost getHttpPost(Map<String, String> headerMap, String url, String bodyStr) {
    HttpPost httpPost = new HttpPost(url);
    if (headerMap != null) {
      headerMap.forEach(httpPost::addHeader);
    }
    httpPost.setEntity(new StringEntity(bodyStr, "UTF-8"));
    return httpPost;
  }


  private HttpClientResponse convert2HttpClientResponse(HttpResponse httpResponse) throws IOException {
    String responseBody = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
    return new HttpClientResponse(httpResponse.getStatusLine().getStatusCode(), responseBody);
  }
}

