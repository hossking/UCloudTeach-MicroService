package cn.gpnusz.liveweb.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.gpnusz.courseinterface.service.CourseMemberService;
import cn.gpnusz.courseinterface.service.CourseService;
import cn.gpnusz.liveinterface.service.LiveTokenService;
import cn.gpnusz.ucloudteachadmin.service.AdminService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.Admin;
import cn.gpnusz.ucloudteachentity.req.ApplyTokenReq;
import cn.gpnusz.ucloudteachentity.resp.LiveTokenResp;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;

/**
 * @author h0ss
 * @description 获取live-token的api
 * @date 2022/4/5 - 17:28
 */
@RestController
public class LiveTokenController {

    @DubboReference(version = "1.0.0")
    private CourseMemberService courseMemberService;

    @DubboReference(version = "1.0.0")
    private LiveTokenService liveTokenService;

    @DubboReference(version = "1.0.0")
    private CourseService courseService;

    @DubboReference(version = "1.0.0")
    private AdminService adminService;

    /**
     * 学生申请token
     *
     * @param applyTokenReq : 申请信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.LiveTokenResp>
     * @author h0ss
     */
    @PostMapping("/api/user/live/token/apply")
    public CommonResp<LiveTokenResp> getTokenStu(@RequestBody ApplyTokenReq applyTokenReq) {
        CommonResp<LiveTokenResp> resp = new CommonResp<>();
        resp.setSuccess(false);
        if (applyTokenReq == null) {
            return resp;
        }
        // 判断用户是否有获取token的权限【是否是开课教师或者学员】
        Long userId = StpUtil.getLoginIdAsLong();
        Boolean isMember = courseMemberService.checkMember(userId, applyTokenReq.getCourseId());
        if (!isMember) {
            return resp;
        }
        // 设置为观众角色
        applyTokenReq.setPublish(Boolean.FALSE);
        // 通过courseId获取channel信息 【这里注意做md5+盐处理一下】
        String channel = DigestUtils.md5DigestAsHex(Long.toString(applyTokenReq.getCourseId()).getBytes(StandardCharsets.UTF_8));
        applyTokenReq.setChannel(channel);
        // 开始生成token
        return liveTokenService.getToken(applyTokenReq);
    }

    /**
     * 教师申请token
     *
     * @param applyTokenReq : 申请信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.LiveTokenResp>
     * @author h0ss
     */
    @PostMapping("/api/admin/live/token/apply")
    public CommonResp<LiveTokenResp> getTokenTea(@NotNull @RequestBody ApplyTokenReq applyTokenReq) {
        CommonResp<LiveTokenResp> resp = new CommonResp<>();
        resp.setSuccess(false);
        // 根据id查询用户名
        Long userId = StpUtil.getLoginIdAsLong();
        Admin info = adminService.getInfoById(userId);
        // 判断用户是否为开课教师
        if (info == null) {
            return resp;
        }
        Boolean isTeacher = courseService.checkTeacher(info.getUsername(), applyTokenReq.getCourseId());
        if (!isTeacher) {
            return resp;
        }
        // 可推流
        applyTokenReq.setPublish(Boolean.TRUE);
        // 通过courseId获取channel信息 【这里注意做md5+盐处理一下】
        String channel = applyTokenReq.getChannel();
        if (applyTokenReq.getChannel() == null) {
            channel = DigestUtils.md5DigestAsHex(Long.toString(applyTokenReq.getCourseId()).getBytes(StandardCharsets.UTF_8));
        }
        applyTokenReq.setChannel(channel);
        // 开始生成token
        return liveTokenService.getToken(applyTokenReq);
    }


}
