package cn.gpnusz.examweb.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.gpnusz.examinterface.service.ExamCheckService;
import cn.gpnusz.examinterface.service.ExamInfoService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageReq;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.req.ExamCheck;
import cn.gpnusz.ucloudteachentity.resp.ExamCust;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author h0ss
 * @description 操作考试信息的接口
 * @date 2021/11/20 17:58
 */

@RestController
@RequestMapping("/api/admin/exam")
public class ExamInfoController {

    @DubboReference(version = "1.0.0")
    private ExamInfoService examInfoService;

    @DubboReference(version = "1.0.0")
    private ExamCheckService examCheckService;

    /**
     * 获取考试信息
     *
     * @param pageReq : 分页参数
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.ExamCust>>
     * @author h0ss
     */
    @GetMapping("/list")
    public CommonResp<PageResp<ExamCust>> search(@Valid PageReq pageReq) {
        CommonResp<PageResp<ExamCust>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(examInfoService.getAll(pageReq));
        return resp;
    }

    /**
     * 暂存批阅信息
     *
     * @param examCheck : 批阅信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/check/store")
    public CommonResp<Object> storeCheck(@Valid @RequestBody ExamCheck examCheck) {
        Long userId = StpUtil.getLoginIdAsLong();
        return examCheckService.storeCheck(userId, examCheck);
    }

    /**
     * 提交批阅信息
     *
     * @param examCheck : 批阅信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/check/submit")
    public CommonResp<Object> submitCheck(@Valid @RequestBody ExamCheck examCheck) {
        Long userId = StpUtil.getLoginIdAsLong();
        return examCheckService.submitCheck(userId, examCheck);
    }

    /**
     * 加载缓存批阅信息
     *
     * @param examId : 考试id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.req.ExamCheck>
     * @author h0ss
     */
    @GetMapping("/check/load")
    public CommonResp<ExamCheck> loadCheck(@NotNull Long examId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return examCheckService.loadCheck(userId, examId);
    }

}
