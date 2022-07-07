package cn.gpnusz.courseweb.controller;

import cn.gpnusz.courseinterface.service.CommonCommentService;
import cn.gpnusz.courseinterface.service.CoursePeriodService;
import cn.gpnusz.courseinterface.service.CourseSectionService;
import cn.gpnusz.courseinterface.service.CourseService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CourseSection;
import cn.gpnusz.ucloudteachentity.req.CourseCommentQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseCommonReq;
import cn.gpnusz.ucloudteachentity.req.CourseSectionQueryReq;
import cn.gpnusz.ucloudteachentity.resp.CommonCommentResp;
import cn.gpnusz.ucloudteachentity.resp.CommonCoursePeriodResp;
import cn.gpnusz.ucloudteachentity.resp.CourseCustResp;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author h0ss
 * @description 供所有用户访问的公共课程接口
 * @date 2021/11/22 - 17:42
 */
@RestController
@RequestMapping("/api/common/course")
public class CourseCommonController {

    @DubboReference(version = "1.0.0")
    private CourseService courseService;

    @DubboReference(version = "1.0.0")
    private CourseSectionService courseSectionService;

    @DubboReference(version = "1.0.0")
    private CoursePeriodService coursePeriodService;

    @DubboReference(version = "1.0.0")
    private CommonCommentService commonCommentService;

    /**
     * 首页获取热门数据的公共接口
     *
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.util.List<cn.gpnusz.ucloudteachentity.resp.CourseCustResp>>
     * @author h0ss
     */
    @GetMapping("/hot")
    public CommonResp<List<CourseCustResp>> getHot() {
        CommonResp<List<CourseCustResp>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(courseService.getHot());
        return resp;
    }

    /**
     * 课程页中根据年级信息获取课程数据的接口
     *
     * @param gradeId   : 年级id
     * @param sortField : 排序字段
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.util.List<cn.gpnusz.ucloudteachentity.resp.CourseCustResp>>
     * @author h0ss
     */
    @GetMapping("/grade/course")
    public CommonResp<List<CourseCustResp>> getByGrade(@NotNull Long gradeId, String sortField) {
        CommonResp<List<CourseCustResp>> resp = new CommonResp<>();
        CourseCommonReq req = new CourseCommonReq();
        req.setGradeId(gradeId);
        req.setSortField(sortField);
        resp.setContent(courseService.getContent(req));
        resp.setMessage("数据获取成功!");
        return resp;
    }

    /**
     * 根据课程id获取课程章节数据的接口
     *
     * @param courseId : 课程id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CourseSection>>
     * @author h0ss
     */
    @GetMapping("/section")
    public CommonResp<PageResp<CourseSection>> getSection(@NotNull Long courseId) {
        CourseSectionQueryReq req = new CourseSectionQueryReq();
        req.setCourseId(courseId);
        CommonResp<PageResp<CourseSection>> resp = new CommonResp<>();
        resp.setContent(courseSectionService.getAll(req));
        resp.setMessage("数据获取成功!");
        return resp;
    }

    /**
     * 根据课程id获取课时公共信息的接口
     *
     * @param courseId : 课程id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.util.List<cn.gpnusz.ucloudteachentity.resp.CommonCoursePeriodResp>>
     * @author h0ss
     */
    @GetMapping("/period")
    public CommonResp<List<CommonCoursePeriodResp>> getPeriod(@NotNull Long courseId) {
        CommonResp<List<CommonCoursePeriodResp>> resp = new CommonResp<>();
        resp.setContent(coursePeriodService.getAllCommon(courseId));
        resp.setMessage("数据获取成功!");
        return resp;
    }

    /**
     * 根据课程id获取课程信息的公共接口
     *
     * @param courseId : 课程id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.CourseCustResp>
     * @author h0ss
     */
    @GetMapping("/content")
    public CommonResp<CourseCustResp> getDetail(@NotNull Long courseId) {
        CommonResp<CourseCustResp> resp = new CommonResp<>();
        CourseCommonReq req = new CourseCommonReq();
        req.setCourseId(courseId);
        resp.setContent(courseService.getContent(req).get(0));
        resp.setMessage("数据获取成功!");
        return resp;
    }

    @GetMapping("/comment/get")
    public CommonResp<List<CommonCommentResp>> getComment(CourseCommentQueryReq courseCommentQueryReq) {
        CommonResp<List<CommonCommentResp>> resp = new CommonResp<>();
        resp.setContent(commonCommentService.getComments(courseCommentQueryReq));
        resp.setMessage("数据获取成功!");
        return resp;
    }

    /**
     * 获取日榜信息
     *
     * @param day : 日期数
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.util.Map<java.lang.Long,cn.gpnusz.ucloudteachentity.resp.CourseCustResp>>
     * @author h0ss
     */
    @GetMapping("/day-hot")
    public CommonResp<Map<Long, CourseCustResp>> getDayHot(Integer day) throws InterruptedException {
        CommonResp<Map<Long, CourseCustResp>> resp = new CommonResp<>();
        resp.setContent(courseService.getTodayHot(day));
        resp.setMessage("数据获取成功!");
        return resp;
    }
}
