package cn.gpnusz.courseweb.controller;

import cn.gpnusz.courseinterface.service.GradeService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.Grade;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author h0ss
 * @description 公共接口获取年级信息
 * @date 2021/11/24 - 8:44
 */
@RestController
@RequestMapping("/api/common/grade")
public class GradeCommonController {

    @DubboReference(version = "1.0.0")
    private GradeService gradeService;

    /**
     * 获取年级信息的接口
     *
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.util.List<cn.gpnusz.ucloudteachentity.entity.Grade>>
     * @author h0ss
     */
    @GetMapping("/list")
    public CommonResp<List<Grade>> list() {
        CommonResp<List<Grade>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(gradeService.getAll());
        return resp;
    }

}
