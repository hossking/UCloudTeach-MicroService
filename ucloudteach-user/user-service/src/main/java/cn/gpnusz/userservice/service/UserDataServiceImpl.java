package cn.gpnusz.userservice.service;


import cn.dev33.satoken.stp.StpUtil;
import cn.gpnusz.ucloudteachentity.entity.StudentInfo;
import cn.gpnusz.ucloudteachentity.req.UserDataSaveReq;
import cn.gpnusz.ucloudteachentity.resp.UserLoginResp;
import cn.gpnusz.userinterface.service.UserDataService;
import cn.gpnusz.userservice.entity.StudentInfoExample;
import cn.gpnusz.userservice.mapper.StudentInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author h0ss
 * @description 用户修改个人信息的业务层
 * @date 2021/11/27 14:24
 */
@DubboService(interfaceClass = UserDataService.class, version = "1.0.0", timeout = 10000)
public class UserDataServiceImpl implements UserDataService {
    @Resource
    private StudentInfoMapper studentInfoMapper;

    /**
     * 用户编辑个人信息的业务方法
     *
     * @param userDataSaveReq : 更新的信息
     * @author h0ss
     */
    @Override
    public void updateInfo(UserDataSaveReq userDataSaveReq) {
        // 拿到当前用户的id，如果未登录会抛出异常，因此后续无需对id判空
        Long id = StpUtil.getLoginIdAsLong();
        // 转换为studentInfo对象
        StudentInfo studentInfo = new StudentInfo();
        BeanUtils.copyProperties(userDataSaveReq, studentInfo);
        if ("".equals(studentInfo.getName())) {
            studentInfo.setName(null);
        }
        // 更新对应id的信息
        StudentInfoExample example = new StudentInfoExample();
        StudentInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        studentInfoMapper.updateByExampleSelective(studentInfo, example);
    }

    /**
     * 通过id获取用户信息【剔除密码和盐值】
     *
     * @param userId : 用户id
     * @return : cn.gpnusz.ucloudteachentity.resp.UserLoginResp
     * @author h0ss
     */
    @Override
    public UserLoginResp getInfoById(Long userId) {
        StudentInfoExample studentInfoExample = new StudentInfoExample();
        StudentInfoExample.Criteria criteria = studentInfoExample.createCriteria();
        criteria.andIdEqualTo(userId);
        List<StudentInfo> res = studentInfoMapper.selectByExample(studentInfoExample);
        UserLoginResp resp = new UserLoginResp();
        if (res != null && !res.isEmpty()) {
            BeanUtils.copyProperties(res.get(0), resp);
            return resp;
        }
        return null;
    }
}
