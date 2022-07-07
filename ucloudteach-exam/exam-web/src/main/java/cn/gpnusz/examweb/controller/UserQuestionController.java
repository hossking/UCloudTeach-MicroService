package cn.gpnusz.examweb.controller;

import cn.gpnusz.examinterface.service.QuestionService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.resp.QuestionAnswerResp;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author h0ss
 * @description 学员获取习题答案的接口
 * @date 2021/12/2 - 1:11
 */
@RestController
@RequestMapping("/api/user/question")
public class UserQuestionController {

    @DubboReference(version = "1.0.0")
    private QuestionService questionService;

    @GetMapping("/answer/get")
    public CommonResp<QuestionAnswerResp> getAnswer(Long id) {
        CommonResp<QuestionAnswerResp> resp = new CommonResp<>();
        resp.setContent(questionService.getAnswer(id));
        resp.setMessage("数据获取成功");
        return resp;
    }
}
