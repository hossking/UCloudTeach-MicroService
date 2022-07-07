package cn.gpnusz.payservice.service;

import cn.gpnusz.payinterface.service.PayCreateService;
import cn.gpnusz.payservice.config.AlipayConfig;
import cn.gpnusz.ucloudteachentity.entity.AlipayBean;
import cn.gpnusz.ucloudteachentity.exception.BusinessException;
import cn.gpnusz.ucloudteachentity.exception.BusinessExceptionCode;
import cn.gpnusz.ucloudteachentity.resp.CourseCustResp;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * @author h0ss
 * @description 创建支付的业务
 * @date 2022/4/5 - 23:29
 */
@DubboService(interfaceClass = PayCreateService.class, version = "1.0.0", timeout = 10000)
public class PayCreateServiceImpl implements PayCreateService {
    @Resource
    private AlipayConfig alipayConfig;

    private static final Logger LOG = LoggerFactory.getLogger(PayCreateServiceImpl.class);

    /**
     * 支付宝申请支付页面
     *
     * @param content  : 课程内容
     * @param recordId : 订单id
     * @param userId   : 用户id
     * @return : java.lang.String
     * @author h0ss
     */
    @Override
    public String aliPay(CourseCustResp content, Long recordId, Long userId) throws BusinessException {
        // 将课程信息写入支付实体
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(String.valueOf(recordId));
        alipayBean.setSubject(content.getName());
        alipayBean.setTotal_amount(String.valueOf(content.getPrice()));
        // 获取当前用户id记录到auth_token中
        alipayBean.setPassback_params(Long.toString(userId));
        // 课程id存入body中
        alipayBean.setBody(String.valueOf(content.getId()));

        // 获得初始化的AlipayClient
        String serverUrl = alipayConfig.gatewayUrl;
        String appId = alipayConfig.appId;
        String privateKey = alipayConfig.privateKey;
        String format = "json";
        String charset = alipayConfig.charset;
        String alipayPublicKey = alipayConfig.publicKey;
        String signType = alipayConfig.signType;
        String returnUrl = alipayConfig.returnUrl;
        String notifyUrl = alipayConfig.notifyUrl;
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);

        // 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 设置页面跳转同步通知页面路径
        alipayRequest.setReturnUrl(returnUrl);
        // 设置服务器异步通知页面路径
        alipayRequest.setNotifyUrl(notifyUrl);
        // 封装参数
        alipayRequest.setBizContent(JSON.toJSONString(alipayBean));
        // 请求支付宝进行付款，返回的是支付页面的html标签数据
        try {
            AlipayTradePagePayResponse res = alipayClient.pageExecute(alipayRequest);
            return res.getBody();
        } catch (AlipayApiException e) {
            LOG.error("发生了支付错误，错误码为 {}，详细信息 {}", e.getErrCode(), e.getErrMsg());
            throw new BusinessException(BusinessExceptionCode.PAY_CREATE_FAIL);
        }
    }
}
