package cn.gpnusz.examweb.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.gpnusz.examinterface.service.UserExamService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.req.UserExamSaveReq;
import cn.gpnusz.ucloudteachentity.resp.UserExamDetailResp;
import cn.gpnusz.ucloudteachentity.resp.UserExamResp;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author h0ss
 * @description 用户考试相关接口
 * @date 2021/11/28 3:38
 */
@RestController
@RequestMapping("/api/user/exam")
public class UserExamController {

    @DubboReference(version = "1.0.0")
    private UserExamService userExamService;


    /**
     * 用户获取考试记录信息的接口
     *
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.util.List<cn.gpnusz.ucloudteach.resp.UserExamResp>>
     * @author h0ss
     */
    @GetMapping("/get")
    public CommonResp<List<UserExamResp>> selectExam() {
        CommonResp<List<UserExamResp>> resp = new CommonResp<>();
        Long userId = StpUtil.getLoginIdAsLong();
        resp.setContent(userExamService.selectUserExam(userId));
        resp.setMessage("数据获取成功");
        return resp;
    }


    /**
     * 获取试卷的试题信息接口
     *
     * @param id : 试卷id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.util.List<cn.gpnusz.ucloudteach.resp.UserExamDetailResp>>
     * @author h0ss
     */
    @GetMapping("/question/get")
    public CommonResp<List<UserExamDetailResp>> getExamDetail(@Valid Long id) {
        CommonResp<List<UserExamDetailResp>> resp = new CommonResp<>();
        Long userId = StpUtil.getLoginIdAsLong();
        resp.setContent(userExamService.getExamDetail(userId, id));
        return resp;
    }

    /**
     * 获取练习题目数据的接口【带答案】
     *
     * @param id : 试卷id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.util.List<cn.gpnusz.ucloudteach.resp.UserExamDetailResp>>
     * @author h0ss
     */
    @GetMapping("/exercise/get")
    public CommonResp<List<UserExamDetailResp>> getExerciseDetail(@Valid Long id) {
        CommonResp<List<UserExamDetailResp>> resp = new CommonResp<>();
        resp.setContent(userExamService.getExerciseDetail(id));
        return resp;
    }

    /**
     * 考试参加状态检测接口
     *
     * @param id : 试卷id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @GetMapping("/check")
    public CommonResp<Object> checkStatus(Long id) {
        CommonResp<Object> resp = new CommonResp<>();
        Long userId = StpUtil.getLoginIdAsLong();
        if (userExamService.checkExamJoinStatus(userId, id)) {
            resp.setSuccess(false);
            resp.setMessage("您已参加过本次考试，请前往个人中心查看");
        }
        return resp;
    }

    /**
     * 学员获取考试结果信息的接口
     *
     * @param paperId :
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<cn.gpnusz.ucloudteach.resp.UserExamResp>
     * @author h0ss
     */
    @GetMapping("/res/get")
    public CommonResp<UserExamResp> getExamRes(Long paperId) {
        CommonResp<UserExamResp> resp = new CommonResp<>();
        Long userId = StpUtil.getLoginIdAsLong();
        resp.setContent(userExamService.getExamRes(userId, paperId));
        resp.setMessage("数据获取成功");
        return resp;
    }

    /**
     * 学员参与考试的接口
     *
     * @param paperInfo : 试卷信息 解析出其中的id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/create")
    public CommonResp<Object> joinExam(@RequestBody Map<String, Long> paperInfo) {
        Long userId = StpUtil.getLoginIdAsLong();
        return userExamService.joinExam(userId, paperInfo.get("paperId"));
    }

    /**
     * 提交考试的接口
     *
     * @param userExamSaveReq : 提交信息
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/submit")
    public CommonResp<Object> submitExam(@RequestBody @Valid UserExamSaveReq userExamSaveReq) {
        Long userId = StpUtil.getLoginIdAsLong();
        return userExamService.submitExam(userId, userExamSaveReq);
    }

    /**
     * 考试提交状态检测的接口
     *
     * @param paperId : 试卷id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @GetMapping("/submit/check")
    public CommonResp<Object> checkSubmit(Long paperId) {
        CommonResp<Object> resp = new CommonResp<>();
        Long userId = StpUtil.getLoginIdAsLong();
        resp.setSuccess(!userExamService.checkSubmit(userId, paperId));
        return resp;
    }

    /**
     * 获取考试剩余时间
     *
     * @param paperId : 试卷id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Integer>
     * @author h0ss
     */
    @GetMapping("/time")
    public CommonResp<Integer> getSurplus(Long paperId) {
        CommonResp<Integer> resp = new CommonResp<>();
        Long userId = StpUtil.getLoginIdAsLong();
        resp.setContent(userExamService.getSurplusTime(userId, paperId));
        resp.setMessage("数据获取成功");
        return resp;
    }

    /**
     * 学生暂存答案
     *
     * @param userExamSaveReq : 答案实体
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/store")
    public CommonResp<Object> storeAnswer(@RequestBody @Valid UserExamSaveReq userExamSaveReq) {
        Long userId = StpUtil.getLoginIdAsLong();
        return userExamService.saveAnswer(userId, userExamSaveReq);
    }

    /**
     * 学生加载暂存的答案
     *
     * @param paperId : 试卷id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    @GetMapping("/load")
    public CommonResp<String> loadAnswer(Long paperId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return userExamService.loadAnswer(userId, paperId);
    }
}
