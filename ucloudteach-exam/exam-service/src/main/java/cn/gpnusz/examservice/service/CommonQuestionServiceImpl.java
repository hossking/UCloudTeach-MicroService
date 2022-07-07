package cn.gpnusz.examservice.service;

import cn.gpnusz.examinterface.service.CommonQuestionService;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.Question;
import cn.gpnusz.ucloudteachentity.req.QuestionQueryReq;
import cn.gpnusz.ucloudteachentity.resp.CommonQuestionResp;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author h0ss
 * @description 公共的题目相关业务层
 * @date 2021/12/1 - 23:03
 */
@DubboService(interfaceClass = CommonQuestionService.class, version = "1.0.0",timeout = 10000)
public class CommonQuestionServiceImpl implements CommonQuestionService {

    @Resource
    private QuestionServiceImpl questionServiceImpl;

    /**
     * 通用的获取题目的方法【屏蔽答案字段】
     *
     * @param questionQueryReq : 查询参数封装
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.CommonQuestionResp>
     * @author h0ss
     */
    @Override
    public PageResp<CommonQuestionResp> getQuestion(QuestionQueryReq questionQueryReq) {
        PageResp<CommonQuestionResp> pageResp = new PageResp<>();
        PageResp<Question> list = questionServiceImpl.getAllByCondition(questionQueryReq);
        if (list.getTotal() > 0) {
            ArrayList<CommonQuestionResp> respList = new ArrayList<>(Math.toIntExact(list.getTotal()));
            for (Question question : list.getList()) {
                CommonQuestionResp resp = new CommonQuestionResp();
                BeanUtils.copyProperties(question, resp);
                respList.add(resp);
            }
            pageResp.setList(respList);
            pageResp.setTotal(list.getTotal());
        }
        return pageResp;
    }
}
