package cn.gpnusz.courseweb.controller;

import cn.gpnusz.courseinterface.service.CourseCommentService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CourseComment;
import cn.gpnusz.ucloudteachentity.req.CourseCommentQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseCommentSaveReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @description 操作课程评论信息的接口
 * @author h0ss
 * @date 2021/11/17 22:41 
 */

@RestController
@RequestMapping("/api/admin/course-comment")
public class CourseCommentController {

    @DubboReference(version = "1.0.0")
    private CourseCommentService courseCommentService;


    /**
     * 按传入条件查询课程评论信息
     * @param courseComment : 查询条件对象
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CourseComment>>
     * @author h0ss
     */
    @GetMapping("/search")
    public CommonResp<PageResp<CourseComment>> search(@Valid CourseCommentQueryReq courseComment) {
        CommonResp<PageResp<CourseComment>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(courseCommentService.getAllByCondition(courseComment));
        return resp;
    }

    /**
     * 新增/编辑课程评论信息
     *
     * @param courseCommentSaveReq : 保存的课程评论信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody CourseCommentSaveReq courseCommentSaveReq) {
        courseCommentService.save(courseCommentSaveReq);
        return new CommonResp<>();
    }

    /**
     * 根据id删除课程评论信息
     *
     * @param id : 课程评论id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        courseCommentService.delete(id);
        return new CommonResp<>();
    }
}
