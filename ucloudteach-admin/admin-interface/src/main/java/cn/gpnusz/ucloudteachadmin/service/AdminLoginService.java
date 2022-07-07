package cn.gpnusz.ucloudteachadmin.service;

import cn.gpnusz.ucloudteachentity.req.AdminLoginReq;
import cn.gpnusz.ucloudteachentity.resp.AdminLoginResp;

/**
 * @author h0ss
 * @description 管理员登录的业务
 * @date 2021/11/23 - 3:15
 */
public interface AdminLoginService {
    /**
     * 管理员登录业务接口
     *
     * @param adminLoginReq : 登录信息
     * @return : cn.gpnusz.ucloudteachentity.resp.AdminLoginResp
     * @author h0ss
     */
    AdminLoginResp adminLogin(AdminLoginReq adminLoginReq);


    /**
     * 管理员检查登录状态的业务方法 如果没有登录会直接抛出NotLoginException异常
     *
     * @author h0ss
     */
    void checkLogin();

    /**
     * 管理员退出登陆的业务方法
     *
     * @author h0ss
     */
    void logout();

}
