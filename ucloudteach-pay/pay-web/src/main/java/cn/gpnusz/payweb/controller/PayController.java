package cn.gpnusz.payweb.controller;

import cn.gpnusz.payinterface.service.PayService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.AliReturnBean;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author h0ss
 * @description 支付api
 * @date 2022/4/6 - 1:27
 */
@RestController
public class PayController {

    @DubboReference(version = "1.0.0")
    private PayService payService;

    /**
     * 利用同步通知内容进行验签 用于页面跳转 不做业务相关操作
     *
     * @param aliReturnBean : 请求信息
     * @author h0ss
     */
    @GetMapping("/api/user/pay/check")
    public CommonResp<Object> checkSign(AliReturnBean aliReturnBean) throws Exception {
        return payService.checkSignService(aliReturnBean);
    }


    /**
     * 开放给支付宝用以异步通知 收到通知之后就可以验签以及其他业务操作了
     *
     * @param request : 请求信息
     * @author h0ss
     */
    @PostMapping("/api/common/pay/notify")
    public void notifyUrl(HttpServletRequest request) throws Exception {
        request.setCharacterEncoding("utf-8");
        //获取支付宝POST过来的反馈信息
        Map<String, String[]> requestParams = request.getParameterMap();
        if (!requestParams.isEmpty() && request.getParameter("trade_status") != null) {
            // 交易状态
            String status = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            payService.validatePayAsync(requestParams, status);
        }
    }
}
