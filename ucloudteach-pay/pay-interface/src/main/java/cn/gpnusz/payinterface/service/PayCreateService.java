package cn.gpnusz.payinterface.service;


import cn.gpnusz.ucloudteachentity.exception.BusinessException;
import cn.gpnusz.ucloudteachentity.resp.CourseCustResp;

/**
 * @author h0ss
 * @description 创建支付订单接口
 * @date 2022/4/5 - 23:29
 */
public interface PayCreateService {
    /**
     * 支付宝申请支付页面
     *
     * @param content  : 课程内容
     * @param recordId : 订单id
     * @param userId   : 用户id
     * @return : java.lang.String
     * @throws BusinessException 创建订单失败业务异常
     */
    String aliPay(CourseCustResp content, Long recordId, Long userId) throws BusinessException;
}
