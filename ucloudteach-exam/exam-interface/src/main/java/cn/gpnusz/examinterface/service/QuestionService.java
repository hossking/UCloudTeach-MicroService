package cn.gpnusz.examinterface.service;


import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.Question;
import cn.gpnusz.ucloudteachentity.req.QuestionQueryReq;
import cn.gpnusz.ucloudteachentity.req.QuestionSaveReq;
import cn.gpnusz.ucloudteachentity.resp.QuestionAnswerResp;

/**
 * @author h0ss
 * @description 操作题目信息的业务层
 * @date 2021/11/18 17:30
 */

public interface QuestionService {
    /**
     * 按传入的条件查询题目
     *
     * @param questionQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.Question>
     * @author h0ss
     */
    PageResp<Question> getAllByCondition(QuestionQueryReq questionQueryReq);

    /**
     * 新增或编辑题目信息的业务方法
     *
     * @param questionSaveReq : 保存的题目信息数据
     * @author h0ss
     */
    void save(QuestionSaveReq questionSaveReq);

    /**
     * 删除题目信息的业务方法
     *
     * @param id : 要删除的题目信息id
     * @author h0ss
     */
    void delete(Long id);


    /**
     * 获取题目答案的业务方法
     *
     * @param id : 题目id
     * @return : cn.gpnusz.ucloudteachentity.resp.QuestionAnswerResp
     * @author h0ss
     */
    QuestionAnswerResp getAnswer(Long id);

    /**
     * 获取试题数量
     *
     * @return : java.lang.Long
     * @author h0ss
     */
    Long getQuestionCount();
}
