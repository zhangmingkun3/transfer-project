package com.lp.transfer.transferproject.utils;

import com.alibaba.fastjson.JSON;
import com.lp.transfer.transferproject.bean.HttpResponse;
import com.lp.transfer.transferproject.bean.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class HttpClientUtils {

    private static final String agent =  "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/51.0.2704.79 Chrome/51.0.2704.79 Safari/537.36";

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


    /**
     * Post 方式请求，返回响应内容
     *
     * @param  address 请求地址
     * @param  params 请求参数
     * @return String 响应内容
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String post(String address, Map<String, Object> params) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(address);
        org.apache.http.HttpResponse httpResponse = null;
        try {


            StringEntity postingString = new StringEntity(JSON.toJSONString(params), "utf-8");
            log.info("http请求参数{}",JSON.toJSONString(params));
            httpPost.setEntity(postingString);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("User-Agent", agent);

            httpResponse = httpClient.execute(httpPost);

            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, HTTP.UTF_8);
        } finally {
            if (httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity()); //会自动释放连接
                } catch (Exception e) {
                }
            }
        }
    }

    private static List<NameValuePair> buildPostData(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return new ArrayList<NameValuePair>(0);
        }
        List<NameValuePair> ret = new ArrayList<NameValuePair>(params.size());
        for (String key : params.keySet()) {
            Object p = params.get(key);
            if (key != null && p != null) {
                NameValuePair np = new BasicNameValuePair(key, p.toString());
                ret.add(np);
            }
        }
        return ret;
    }

}
