package cn.gpnusz.examinterface.service;

import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.req.QuestionQueryReq;
import cn.gpnusz.ucloudteachentity.resp.CommonQuestionResp;

/**
 * @author h0ss
 * @description 公共的题目相关业务层
 * @date 2021/12/1 - 23:03
 */
public interface CommonQuestionService {
    /**
     * 通用的获取题目的方法【屏蔽答案字段】
     *
     * @param questionQueryReq : 查询参数封装
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.CommonQuestionResp>
     * @author h0ss
     */
    PageResp<CommonQuestionResp> getQuestion(QuestionQueryReq questionQueryReq);
}
