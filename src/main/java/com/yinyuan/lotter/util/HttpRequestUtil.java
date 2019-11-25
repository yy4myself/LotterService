package com.yinyuan.lotter.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpRequestUtil {

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        log.info("HttpRequestUtil sendGetRequest url = " + url);
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();

            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error("HttpRequestUtil sendGetRequest error = " + e);
            e.printStackTrace();
            result = null;
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (log.isDebugEnabled()) log.debug("HttpRequestUtil sendGetRequest result = " + result);
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url             发送请求的 URL
     * @param jsonRequestBody 发送请求的requestbody
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, JSONObject jsonRequestBody) {
        log.info("HttpRequestUtil sendPostRequest url = " + url + ";requestBody = " + jsonRequestBody.toJSONString());
        String result = null;

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        StringEntity entity = new StringEntity(jsonRequestBody.toJSONString(), ContentType.APPLICATION_JSON);
        post.setEntity(entity);

        try {
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                result = EntityUtils.toString(responseEntity, "utf-8");
            }
            response.close();
        } catch (ClientProtocolException e) {
            log.error("HttpRequestUtil sendPostRequest ClientProtocolException = " + e);
            e.printStackTrace();
        } catch (IOException e) {
            log.error("HttpRequestUtil sendPostRequest IOException = " + e);
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (log.isDebugEnabled())
            log.debug("HttpRequestUtil sendPostRequest ClientProtocolException result = " + result);
        return result;
    }
}
