package cn.gpnusz.liveweb.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.gpnusz.courseinterface.service.CourseMemberService;
import cn.gpnusz.liveinterface.service.LiveContentService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.resp.LiveTokenResp;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * @author h0ss
 * @description 用户参与直播api
 * @date 2022/4/6 - 12:46
 */
@RestController
@RequestMapping("/api/user/live")
public class UserLiveController {

    @DubboReference(version = "1.0.0")
    private CourseMemberService courseMemberService;

    @DubboReference(version = "1.0.0")
    private LiveContentService liveContentService;

    /**
     * 用户加入直播
     *
     * @param courseId  : 课程id
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.LiveTokenResp>
     * @author h0ss
     */
    @GetMapping("/join")
    public CommonResp<LiveTokenResp> checkLive(@NotNull Long courseId, @NotNull Long contentId) {
        CommonResp<LiveTokenResp> resp = new CommonResp<>();
        long userId = StpUtil.getLoginIdAsLong();
        // 校验学生权限
        Boolean isMember = courseMemberService.checkMember(userId, courseId);
        if (!isMember) {
            resp.setSuccess(false);
            resp.setMessage("请先申请加入课程");
            return resp;
        }
        // 执行加入课堂逻辑
        return liveContentService.joinLive(userId, courseId, contentId);
    }

    /**
     * 用户获取回放信息
     *
     * @param courseId  : 课程id
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    @GetMapping("/back")
    public CommonResp<String> getBackVideo(@NotNull Long courseId, @NotNull Long contentId) {
        CommonResp<String> resp = new CommonResp<>();
        long userId = StpUtil.getLoginIdAsLong();
        // 校验学生权限
        Boolean isMember = courseMemberService.checkMember(userId, courseId);
        if (isMember) {
            // 获取回放信息
            resp.setContent(liveContentService.getContent(contentId).getBackVideo());
            resp.setMessage("获取成功");
            return resp;
        }
        resp.setSuccess(false);
        resp.setMessage("请先参加课程");
        return resp;
    }

    /**
     * 检查直播状态
     *
     * @param courseId  : 课程id
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Boolean>
     * @author h0ss
     */
    @GetMapping("/check")
    public CommonResp<Boolean> checkStatus(@NotNull Long courseId, @NotNull Long contentId) {
        CommonResp<Boolean> resp = new CommonResp<>();
        long userId = StpUtil.getLoginIdAsLong();
        // 校验学生权限
        Boolean isMember = courseMemberService.checkMember(userId, courseId);
        if (isMember) {
            return liveContentService.checkStatus(contentId);
        }
        resp.setSuccess(false);
        resp.setMessage("请先参加课程");
        return resp;
    }


}
