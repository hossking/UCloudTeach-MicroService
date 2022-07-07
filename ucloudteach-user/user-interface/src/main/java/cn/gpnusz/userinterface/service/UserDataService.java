package cn.gpnusz.userinterface.service;


import cn.gpnusz.ucloudteachentity.req.UserDataSaveReq;
import cn.gpnusz.ucloudteachentity.resp.UserLoginResp;

/**
 * @author h0ss
 * @description 用户修改个人信息的业务接口
 * @date 2021/11/27 14:24
 */
public interface UserDataService {
    /**
     * 用户编辑个人信息的业务接口
     *
     * @param userDataSaveReq : 更新的信息
     * @author h0ss
     */
    void updateInfo(UserDataSaveReq userDataSaveReq);

    /**
     * 通过id获取用户信息【剔除密码和盐值】
     *
     * @param userId : 用户id
     * @return : cn.gpnusz.ucloudteachentity.resp.UserLoginResp
     * @author h0ss
     */
    UserLoginResp getInfoById(Long userId);
}
