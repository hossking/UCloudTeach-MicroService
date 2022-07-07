package cn.gpnusz.courseweb.controller;

import cn.gpnusz.courseinterface.service.CoursePeriodService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CoursePeriod;
import cn.gpnusz.ucloudteachentity.req.CoursePeriodQueryReq;
import cn.gpnusz.ucloudteachentity.req.CoursePeriodSaveReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author h0ss
 * @description 课程课时信息接口
 * @date 2021/11/15 0:11
 */

@RestController
@RequestMapping("/api/admin/course-period")
public class CoursePeriodController {

    @DubboReference(version = "1.0.0")
    private CoursePeriodService coursePeriodService;

    /**
     * 展示指定课程下所有课时信息数据
     *
     * @param coursePeriodQueryReq : 请求对象
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CoursePeriod>>
     * @author h0ss
     */
    @GetMapping("/list")
    public CommonResp<PageResp<CoursePeriod>> list(@Valid CoursePeriodQueryReq coursePeriodQueryReq) {
        CommonResp<PageResp<CoursePeriod>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(coursePeriodService.getAll(coursePeriodQueryReq));
        return resp;
    }

    /**
     * 新增/编辑课时信息
     *
     * @param coursePeriodSaveReq : 保存的课时信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody CoursePeriodSaveReq coursePeriodSaveReq) {
        coursePeriodService.save(coursePeriodSaveReq);
        return new CommonResp<>();
    }

    /**
     * 根据id删除课时信息
     *
     * @param id : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        coursePeriodService.delete(id);
        return new CommonResp<>();
    }
}
