package cn.gpnusz.adminweb.controller;

import cn.gpnusz.ucloudteachadmin.service.AdminLoginService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.exception.BusinessExceptionCode;
import cn.gpnusz.ucloudteachentity.req.AdminLoginReq;
import cn.gpnusz.ucloudteachentity.resp.AdminLoginResp;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author h0ss
 * @description 管理员登录接口
 * @date 2021/11/23 - 3:01
 */
@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    @DubboReference(version = "1.0.0")
    private AdminLoginService adminLoginService;

    @PostMapping("/login")
    public CommonResp<AdminLoginResp> login(@RequestBody @Valid AdminLoginReq adminLoginReq) {
        CommonResp<AdminLoginResp> resp = new CommonResp<>();
        resp.setContent(adminLoginService.adminLogin(adminLoginReq));
        return resp;
    }

    @GetMapping("/check-login")
    public CommonResp<Object> checkLogin() {
        adminLoginService.checkLogin();
        return new CommonResp<>();
    }

    @GetMapping("/logout")
    public CommonResp<Object> logout() {
        adminLoginService.logout();
        return new CommonResp<>();
    }
}
