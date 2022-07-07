package cn.gpnusz.userweb.controller;

import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.req.UserDataSaveReq;
import cn.gpnusz.userinterface.service.UserDataService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author h0ss
 * @description 已登录用户对本人信息修改的API
 * @date 2021/11/27 - 14:23
 */
@RestController
@RequestMapping("/api/user/info")
public class UserDataController {

    @DubboReference(version = "1.0.0")
    private UserDataService userDataService;

    /**
     * 用户编辑个人信息
     *
     * @param userDataSaveReq : 请求编辑的信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/save")
    public CommonResp<Object> updateInfo(@RequestBody UserDataSaveReq userDataSaveReq) {
        CommonResp<Object> resp = new CommonResp<>();
        userDataService.updateInfo(userDataSaveReq);
        resp.setMessage("信息更新成功");
        return resp;
    }
}
