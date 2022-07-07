package cn.gpnusz.courseweb.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.gpnusz.courseinterface.service.CourseMemberService;
import cn.gpnusz.courseinterface.service.UserCourseService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.CoursePeriod;
import cn.gpnusz.ucloudteachentity.exception.BusinessException;
import cn.gpnusz.ucloudteachentity.req.CourseCommentSaveReq;
import cn.gpnusz.ucloudteachentity.req.CourseCommonReq;
import cn.gpnusz.ucloudteachentity.resp.UserCourseResp;
import cn.gpnusz.ucloudteachentity.resp.UserLoginResp;
import cn.gpnusz.userinterface.service.UserDataService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author h0ss
 * @description 用户课程相关接口
 * @date 2021/11/28 1:07
 */
@RestController
@RequestMapping("/api/user/course")
public class UserCourseController {

    @DubboReference(version = "1.0.0")
    private UserCourseService userCourseService;

    @DubboReference(version = "1.0.0")
    private CourseMemberService courseMemberService;

    @DubboReference(version = "1.0.0")
    private UserDataService userDataService;


    /**
     * 用户获取学习记录信息的接口
     *
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.util.List<cn.gpnusz.ucloudteachentity.resp.CourseMemberCustResp>>
     * @author h0ss
     */
    @GetMapping("/get")
    public CommonResp<List<UserCourseResp>> selectCourse() {
        CommonResp<List<UserCourseResp>> resp = new CommonResp<>();
        Long userId = StpUtil.getLoginIdAsLong();
        resp.setContent(userCourseService.selectUserCourse(userId));
        resp.setMessage("数据获取成功");
        return resp;
    }

    /**
     * 根据课时id获取课时详情的接口
     *
     * @param periodId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.entity.CoursePeriod>
     * @author h0ss
     */
    @GetMapping("/period/content")
    public CommonResp<CoursePeriod> getPeriodContent(@NotNull Long periodId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return userCourseService.getContent(userId, periodId);
    }

    /**
     * 检查用户是否购买课程
     *
     * @param courseId : 课程id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Boolean>
     * @author h0ss
     */
    @GetMapping("/check")
    public CommonResp<Boolean> checkStatus(@NotNull Long courseId) {
        CommonResp<Boolean> resp = new CommonResp<>();
        Long userId = StpUtil.getLoginIdAsLong();
        Boolean status = courseMemberService.checkMember(userId, courseId);
        resp.setMessage("数据获取成功");
        resp.setContent(status);
        return resp;
    }

    /**
     * 用户保存评论信息的接口
     *
     * @param courseCommentSaveReq : 保存信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/comment/save")
    public CommonResp<Object> saveComment(@Valid @RequestBody CourseCommentSaveReq courseCommentSaveReq) {
        CommonResp<Object> resp = new CommonResp<>();
        Long userId = StpUtil.getLoginIdAsLong();
        userCourseService.saveComment(userId, courseCommentSaveReq);
        resp.setMessage("评论发表成功");
        return resp;
    }

    /**
     * 用户删除评论的业务方法
     *
     * @param id : 评论id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @DeleteMapping("/comment/delete/{id}")
    public CommonResp<Object> deleteComment(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        return userCourseService.deleteComment(userId, id);
    }

    /**
     * 用户获取证书的接口
     *
     * @param courseId : 课程id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @GetMapping("/certificate/get")
    public CommonResp<Object> getCertificate(Long courseId, String mail) {
        Long userId = StpUtil.getLoginIdAsLong();
        UserLoginResp info = userDataService.getInfoById(userId);
        if (info != null) {
            return userCourseService.applyCertificate(userId, info.getName(), mail, courseId);
        }
        CommonResp<Object> resp = new CommonResp<>();
        resp.setSuccess(false);
        resp.setMessage("申请失败，用户信息读取错误！");
        return resp;
    }

    /**
     * 用户申请参与课程的接口
     *
     * @param req : 课程信息【id】
     * @return : java.lang.String
     * @author h0ss
     */
    @PostMapping("/join")
    public CommonResp<String> joinCourse(@RequestBody CourseCommonReq req) {
        Long userId = StpUtil.getLoginIdAsLong();
        return userCourseService.joinCourse(req, userId);
    }
}
