package com.lp.transfer.transferproject.utils;

import com.alibaba.fastjson.JSON;
import com.lp.transfer.transferproject.bean.HttpResponse;
import com.lp.transfer.transferproject.bean.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

@Slf4j
public class HttpClientUtils {

    private static HttpGet defaultGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(defaultConfig());
        return httpGet;
    }

    private static HttpPost defaultPost(String url) {
        HttpPost httpGet = new HttpPost(url);
        httpGet.setConfig(defaultConfig());
        return httpGet;
    }

    private static RequestConfig defaultConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(1000)
                .setConnectionRequestTimeout(1000)
                .setSocketTimeout(1000).build();
    }


    private static HttpPost defaultPost(String url, String body) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(defaultConfig());
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        return httpPost;
    }

    public static void httpGetRequest(String url, Consumer<String> consumer) throws IOException {
        httpRequest(HttpClients.createDefault(), defaultGet(url), consumer);
    }

    public static HttpResponse httpGetRequest(String url) throws IOException {
        return httpRequest(HttpClients.createDefault(), defaultGet(url));
    }

    public static void httpPostRequest(String url, String body,  Consumer<String> consumer) throws IOException {
        httpRequest(HttpClients.createDefault(), defaultPost(url, body), consumer);
    }


    public static HttpResponse httpPostRequest(String url, String body) throws IOException {
        return httpRequest(HttpClients.createDefault(), defaultPost(url, body));
    }


    /**
     * 发起HTTP请求
     */
    public static HttpResponse httpRequest(CloseableHttpClient httpClient, HttpRequestBase httpRequest) throws IOException {
        CloseableHttpResponse response = httpClient.execute(httpRequest);
        HttpEntity entity = response.getEntity();

        HttpResponse res = new HttpResponse();
        res.setStatusCode(response.getStatusLine().getStatusCode());
        res.setSuccess(HttpStatus.isSuccess(res.getStatusCode()));
        if (null != entity) {
            StringBuilder sb = new StringBuilder();
            new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8")).lines().forEach(sb::append);
            res.setContent(sb.toString());
        }
        if (!res.isSuccess()) {
            log.warn("http请求失败,url:{},response:{}", httpRequest.getURI(), JSON.toJSONString(res));
        }
        return res;
    }

    /**
     * 发起HTTP请求
     */
    public static void httpRequest(CloseableHttpClient httpClient, HttpRequestBase httpGet, Consumer<String> consumer) throws IOException {
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (HttpStatus.isSuccess(response.getStatusLine().getStatusCode())) {
            StringBuilder sb = new StringBuilder();
            if (null != entity.getContent()) {
                new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8")).lines().forEach(sb::append);
            }
            consumer.accept(sb.toString());
        } else {
            throw new IOException("http request not success, status : " + response.getStatusLine().getStatusCode());
        }
    }
}
