package cn.gpnusz.courseweb.controller;

import cn.gpnusz.courseinterface.service.CourseService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.Course;
import cn.gpnusz.ucloudteachentity.req.CourseQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseSaveReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author h0ss
 * @description 课程信息接口
 * @date 2021/11/14 20:22
 */

@RestController
@RequestMapping("/api/admin/course")
public class CourseController {

    @DubboReference(version = "1.0.0")
    private CourseService courseService;

    /**
     * 展示所有课程信息数据
     *
     * @param course : 请求参数（分页参数）
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.Course>>
     * @author h0ss
     */
    @GetMapping("/list")
    public CommonResp<PageResp<Course>> list(@Valid CourseQueryReq course) {
        CommonResp<PageResp<Course>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(courseService.getAll(course));
        return resp;
    }

    /**
     * 按传入条件查询课程信息
     *
     * @param course : 课程信息查询参数
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.Course>>
     * @author h0ss
     */
    @GetMapping("/search")
    public CommonResp<PageResp<Course>> search(@Valid CourseQueryReq course) {
        CommonResp<PageResp<Course>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(courseService.getAllByCondition(course));
        return resp;
    }

    /**
     * 新增/编辑课程信息
     *
     * @param courseSaveReq : 保存的课程信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody CourseSaveReq courseSaveReq) {
        courseService.save(courseSaveReq);
        return new CommonResp<>();
    }

    /**
     * 根据id删除课程信息
     *
     * @param id : 课程id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        courseService.delete(id);
        return new CommonResp<>();
    }
}
