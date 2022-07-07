package cn.gpnusz.payinterface.service;


import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.AliReturnBean;

import java.util.Map;


/**
 * @author h0ss
 * @description 课程支付业务层
 * @date 2021/11/28 - 18:46
 */
public interface PayService {
    /**
     * 利用同步通知信息进行验签的业务方法
     *
     * @param aliReturnBean : 验签信息
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @throws Exception 支付业务异常
     * @author h0ss
     */
    CommonResp<Object> checkSignService(AliReturnBean aliReturnBean) throws Exception;

    /**
     * 支付宝异步通知验签 在验签完成之后进行相关业务操作
     *
     * @param requestParams : 支付宝请求参数
     * @param status        : 交易状态
     * @throws Exception 支付业务异常
     * @author h0ss
     */
    void validatePayAsync(Map<String, String[]> requestParams, String status) throws Exception;
}