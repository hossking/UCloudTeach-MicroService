package cn.gpnusz.courseweb.controller;

import cn.gpnusz.courseinterface.service.SubjectService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.Subject;
import cn.gpnusz.ucloudteachentity.req.SubjectSaveReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author h0ss
 * @description 科目信息接口
 * @date 2021/11/14 - 1:58
 */

@RestController
@RequestMapping("/api/admin/subject")
public class SubjectController {

    @DubboReference(version = "1.0.0")
    private SubjectService subjectService;

    /**
     * 获取科目信息的接口
     *
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.util.List<cn.gpnusz.ucloudteachentity.entity.Subject>>
     * @author h0ss
     */
    @GetMapping("/list")
    public CommonResp<List<Subject>> list() {
        CommonResp<List<Subject>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(subjectService.getAll());
        return resp;
    }

    /**
     * 保存科目信息的接口
     *
     * @param subjectSaveReq : 保存的科目信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody SubjectSaveReq subjectSaveReq) {
        subjectService.save(subjectSaveReq);
        return new CommonResp<>();
    }

    /**
     * 删除科目信息的接口
     *
     * @param id : 科目id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return new CommonResp<>();
    }

}
