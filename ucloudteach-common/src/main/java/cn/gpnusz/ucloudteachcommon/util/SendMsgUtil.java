package cn.gpnusz.ucloudteachcommon.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author h0ss
 * @description 短信发送工具类
 * @date 2021/11/26 - 15:04
 */
public class SendMsgUtil {

    @Value("${msg-key.app-key}")
    private static String appKey;

    @Value("${msg-key.url}")
    private static String url;


    public static JSONObject sendMsg(String phone, String code) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        String curTime = String.valueOf(System.currentTimeMillis());
        String nonce = RandomKeyUtil.getRandomSalt(16);
        // 鉴权
        String authorization = DigestUtils.md5DigestAsHex((appKey + curTime + nonce).getBytes(StandardCharsets.UTF_8));
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("nonce", nonce));
        nvps.add(new BasicNameValuePair("timestamp", curTime));
        nvps.add(new BasicNameValuePair("authorization", authorization));
        nvps.add(new BasicNameValuePair("phone", phone));
        nvps.add(new BasicNameValuePair("captcha", code));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        String res = EntityUtils.toString(response.getEntity(), "utf-8");

        return JSON.parseObject(res);
    }
}
