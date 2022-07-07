package cn.gpnusz.liveweb.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.gpnusz.courseinterface.service.CourseService;
import cn.gpnusz.liveinterface.service.LiveContentService;
import cn.gpnusz.ucloudteachadmin.service.AdminService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.Admin;
import cn.gpnusz.ucloudteachentity.resp.LiveTokenResp;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author h0ss
 * @description 教师直播api
 * @date 2022/4/6 - 14:16
 */
@RestController
@RequestMapping("/api/admin/live")
public class AdminLiveController {

    @DubboReference(version = "1.0.0")
    private AdminService adminService;

    @DubboReference(version = "1.0.0")
    private CourseService courseService;

    @DubboReference(version = "1.0.0")
    private LiveContentService liveContentService;

    /**
     * 教师开始直播
     *
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.LiveTokenResp>
     * @author h0ss
     */
    @PostMapping("/begin")
    public CommonResp<LiveTokenResp> startLive(@RequestBody Map<String, Long> courseAndContentReq) {
        CommonResp<LiveTokenResp> resp = new CommonResp<>();
        Long courseId = courseAndContentReq.get("courseId");
        // 根据id查询用户名
        Long userId = StpUtil.getLoginIdAsLong();
        Admin info = adminService.getInfoById(userId);
        // 判断用户是否为开课教师
        if (info == null || !courseService.checkTeacher(info.getUsername(), courseId)) {
            resp.setSuccess(false);
            resp.setMessage("请求受限：非开课教师");
            return resp;
        }
        Long contentId = courseAndContentReq.get("contentId");
        return liveContentService.startLive(userId, courseId, contentId);
    }

    /**
     * 教师结束直播
     *
     * @param courseAndContentReq  : 课程id & 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/finish")
    public CommonResp<Object> finishLive(@NotNull @RequestBody Map<String,Long> courseAndContentReq) {
        CommonResp<Object> resp = new CommonResp<>();
        Long userId = StpUtil.getLoginIdAsLong();
        Long courseId = courseAndContentReq.get("courseId");
        Long contentId = courseAndContentReq.get("contentId");
        liveContentService.finishLive(userId, courseId, contentId);
        resp.setMessage("直播已结束，回放正在生成");
        return resp;
    }
}
