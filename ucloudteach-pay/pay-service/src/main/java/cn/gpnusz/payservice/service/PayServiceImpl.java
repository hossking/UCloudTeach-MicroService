package cn.gpnusz.payservice.service;


import cn.gpnusz.courseinterface.service.UserCourseService;
import cn.gpnusz.payinterface.service.PayService;
import cn.gpnusz.payservice.config.AlipayConfig;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.AliReturnBean;
import cn.gpnusz.ucloudteachentity.entity.CourseRecord;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author h0ss
 * @description 课程支付业务层
 * @date 2021/11/28 - 18:46
 */
@DubboService(interfaceClass = PayService.class, version = "1.0.0", timeout = 10000)
public class PayServiceImpl implements PayService {

    @Resource
    private AlipayConfig alipayConfig;

    @DubboReference(version = "1.0.0", check = false)
    private UserCourseService userCourseService;

    private static final Logger LOG = LoggerFactory.getLogger(PayServiceImpl.class);


    /**
     * 利用同步通知信息进行验签的业务方法
     *
     * @param aliReturnBean : 验签信息
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @Override
    public CommonResp<Object> checkSignService(AliReturnBean aliReturnBean) throws AlipayApiException {
        System.out.println(aliReturnBean);
        CommonResp<Object> resp = new CommonResp<>();
        LOG.info("支付宝同步通知跳转验签");
        boolean signVerified = AlipaySignature.rsaCheck(aliReturnBean.toString(), aliReturnBean.getSign(), alipayConfig.publicKey, alipayConfig.charset, alipayConfig.signType);
        if (signVerified) {
            LOG.info("验签成功-跳转到成功后页面");
            resp.setMessage("支付成功");
            resp.setSuccess(true);
        } else {
            LOG.info("验签失败-跳转到失败页面");
            resp.setMessage("支付失败");
            resp.setSuccess(false);
        }
        return resp;
    }

    /**
     * 支付宝异步通知验签 在验签完成之后进行相关业务操作
     *
     * @param requestParams : 支付宝请求参数
     * @author h0ss
     */
    @Override
    public void validatePayAsync(Map<String, String[]> requestParams, String status) throws AlipayApiException {
        LOG.info("接受支付宝异步通知，开始验签并准备相关业务操作");
        Map<String, String> params = new HashMap<>(requestParams.size());
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String[] values = entry.getValue();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                if (i == values.length - 1) {
                    sb.append(values[i]);
                } else {
                    sb.append(values[i]);
                    sb.append(',');
                }
            }
            params.put(entry.getKey(), sb.toString());
        }
        // 验签
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.publicKey, alipayConfig.charset, alipayConfig.signType);
        // 验签成功执行业务逻辑
        if (signVerified) {
            LOG.info("验签成功，开始执行业务方法");
            // 交易状态
            if ("TRADE_SUCCESS".equals(status)) {
                // 调用业务方法 写入课程成员信息
                userCourseService.addMemberInfo(Long.valueOf(params.get("passback_params")), Long.valueOf(params.get("body")), Long.valueOf(params.get("out_trade_no")));
                // 生成订单对象
                CourseRecord courseRecord = userCourseService.generateRecord(
                        Long.valueOf(params.get("body")),
                        BigDecimal.valueOf(Double.parseDouble(params.get("receipt_amount"))),
                        Long.parseLong(params.get("out_trade_no")),
                        Long.valueOf(params.get("passback_params"))
                );
                // 发送订单消息
                userCourseService.sendOrderToMq(courseRecord, "order.finish", "orderFinish");
            }
        }
    }
}