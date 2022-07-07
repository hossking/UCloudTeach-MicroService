package cn.gpnusz.liveinterface.service;

import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.req.ApplyTokenReq;
import cn.gpnusz.ucloudteachentity.resp.LiveTokenResp;

/**
 * @author h0ss
 * @description 直播token获取接口
 * @date 2022/4/5 - 16:26
 */
public interface LiveTokenService {

    /**
     * 获取直播间token的接口
     *
     * @param applyTokenReq : 申请信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.LiveTokenResp>
     * @author h0ss
     */
    CommonResp<LiveTokenResp> getToken(ApplyTokenReq applyTokenReq);

}
