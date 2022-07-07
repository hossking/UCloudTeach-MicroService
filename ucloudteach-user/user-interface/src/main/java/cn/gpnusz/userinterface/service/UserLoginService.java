package cn.gpnusz.userinterface.service;

import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.req.UserLoginReq;
import cn.gpnusz.ucloudteachentity.req.UserRegisterReq;
import cn.gpnusz.ucloudteachentity.resp.UserLoginResp;

/**
 * @author h0ss
 * @description 用户登录业务接口
 * @date 2021/11/26 1:52
 */
public interface UserLoginService {
    /**
     * 用户登录的业务接口
     *
     * @param userLoginReq : 用户登录信息
     * @return : cn.gpnusz.ucloudteachentity.resp.UserLoginResp
     * @author h0ss
     */
    UserLoginResp userLogin(UserLoginReq userLoginReq);

    /**
     * 检查登录状态的业务接口 如果未登录则抛NotLoginException异常
     *
     * @author h0ss
     */
    void checkLogin();

    /**
     * 用户退出登陆的业务接口
     *
     * @author h0ss
     */
    void logout();


    /**
     * 注册用户的业务接口
     *
     * @param userRegisterReq : 用户注册信息实体类
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> userRegister(UserRegisterReq userRegisterReq);

    /**
     * 用户找回密码的业务接口
     *
     * @param userRegisterReq : 请求信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> resetPasswd(UserRegisterReq userRegisterReq);

    /**
     * 请求获取验证码的业务接口
     *
     * @param phone : 手机号码
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> requestSend(String phone);

    /**
     * 检查注册手机号是否已存在
     *
     * @param phone :  手机号
     * @return : java.lang.Boolean
     * @author h0ss
     */
    Boolean checkUser(String phone);
}
