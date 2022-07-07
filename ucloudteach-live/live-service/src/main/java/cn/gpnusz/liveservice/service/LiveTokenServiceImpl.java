package cn.gpnusz.liveservice.service;

import cn.gpnusz.liveinterface.service.LiveTokenService;
import cn.gpnusz.liveservice.util.token.media.RtcTokenBuilder;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.req.ApplyTokenReq;
import cn.gpnusz.ucloudteachentity.resp.LiveTokenResp;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author h0ss
 * @description 直播token获取
 * @date 2022/4/5 - 16:26
 */
@Service
@DubboService(interfaceClass = LiveTokenService.class, version = "1.0.0", timeout = 10000)
public class LiveTokenServiceImpl implements LiveTokenService {

    private static final int EXPIRATION_TIME_IN_SECONDS = 300;
    /**
     * 声网授权配置
     */
    private static final String CERT = "123";

    private static final String APP_ID = "123";

    /**
     * 获取直播间token信息
     *
     * @param applyTokenReq : 申请信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.LiveTokenResp>
     * @author h0ss
     */
    @Override
    public CommonResp<LiveTokenResp> getToken(ApplyTokenReq applyTokenReq) {
        CommonResp<LiveTokenResp> resp = new CommonResp<>();
        String channelName = applyTokenReq.getChannel();
        RtcTokenBuilder.Role role = applyTokenReq.getPublish() ? RtcTokenBuilder.Role.Role_Publisher : RtcTokenBuilder.Role.Role_Subscriber;
        int timestamp = (int) (System.currentTimeMillis() / 1000 + EXPIRATION_TIME_IN_SECONDS);
        RtcTokenBuilder token = new RtcTokenBuilder();
        String result = token.buildTokenWithUid(APP_ID, CERT, channelName, 0, role, timestamp);
        String roleResult = applyTokenReq.getPublish() ? "host" : "audience";
        LiveTokenResp ltr = new LiveTokenResp(result, channelName, 0, roleResult);
        resp.setContent(ltr);
        return resp;
    }

}
