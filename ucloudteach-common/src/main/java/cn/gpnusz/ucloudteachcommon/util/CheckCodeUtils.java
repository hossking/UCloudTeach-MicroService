package cn.gpnusz.ucloudteachcommon.util;

import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author h0ss
 * @description 腾讯云验证码后端校验
 * @date 2021/12/5 - 13:36
 */
@PropertySource("classpath:tencent-application.yml")
public class CheckCodeUtils {

    @Value("${SecretId}")
    private static String secretId;

    @Value("${SecretKey}")
    private static String secretKey;

    @Value("${AppSecretKey}")
    private static String appSecretKey;

    @Value("${CaptchaAppId}")
    private static Long captchaAppId;


    public static Boolean checkRes(String ticket, String randStr) {
        try {
            String remoteIp = getIp();
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(secretId, secretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("captcha.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            CaptchaClient client = new CaptchaClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DescribeCaptchaResultRequest req = new DescribeCaptchaResultRequest();
            req.setCaptchaType(9L);
            req.setTicket(ticket);
            req.setUserIp(remoteIp);
            req.setRandstr(randStr);
            req.setCaptchaAppId(captchaAppId);
            req.setAppSecretKey(appSecretKey);
            // 返回的resp是一个DescribeCaptchaResultResponse的实例，与请求对象对应
            DescribeCaptchaResultResponse resp = client.DescribeCaptchaResult(req);
            // 返回结果码
            return resp.getCaptchaCode().equals(1L);
        } catch (TencentCloudSDKException e) {
            return Boolean.FALSE;
        }
    }

    public static String getIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        if (request != null) {
            return request.getRemoteAddr().replaceAll("[\r\n]", "");
        }
        return "";
    }
}