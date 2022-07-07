package cn.gpnusz.examinterface.service;

import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.ExamPaper;
import cn.gpnusz.ucloudteachentity.req.ExamPaperQueryReq;
import cn.gpnusz.ucloudteachentity.req.ExamPaperSaveReq;
import cn.gpnusz.ucloudteachentity.req.PaperQuestionSaveReq;
import cn.gpnusz.ucloudteachentity.resp.QuestionCust;

import java.util.List;

/**
 * @author h0ss
 * @description 操作试卷信息的业务层
 * @date 2021/11/19 19:25
 */

public interface ExamPaperService {
    /**
     * 按传入的条件查询试卷
     *
     * @param examPaperQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.ExamPaper>
     * @author h0ss
     */
    PageResp<ExamPaper> getAllByCondition(ExamPaperQueryReq examPaperQueryReq);

    /**
     * 新增或编辑试卷信息的业务方法
     *
     * @param examPaperSaveReq : 保存的试卷信息数据
     * @author h0ss
     */
    void save(ExamPaperSaveReq examPaperSaveReq);

    /**
     * 删除试卷信息的业务方法
     *
     * @param id : 要删除的试卷信息id
     * @author h0ss
     */
    void delete(Long id);


    /**
     * 考试人数增长的业务方法
     *
     * @param id : 试卷id
     * @author h0ss
     */
    void plusJoinCount(Long id);

    /**
     * 批阅数增长的业务方法
     *
     * @param paperId : 试卷id
     * @author h0ss
     */
    void plusCheckCount(Long paperId);

    /**
     * 查询试卷对应的题目的业务方法
     *
     * @param paperId : 试卷id
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.resp.QuestionCust>
     * @author h0ss
     */
    List<QuestionCust> getQuestions(Long paperId);

    /**
     * 保存试卷-题目关联信息的业务方法
     *
     * @param paperQuestionSaveReq : 试卷-题目关联信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> savePaperAndQuestion(PaperQuestionSaveReq paperQuestionSaveReq);

    /**
     * 从试卷中删除题目的业务方法
     *
     * @param id : 要删除的试卷-题目关联信息id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> deletePaperAndQuestion(Long id);

    /**
     * 更新试卷中的试题数
     * 查看更新频度 频繁就不应该在db操作[0407]
     *
     * @param paperId : 试卷id
     * @author h0ss
     */
    void updateQuestionCount(Long paperId);

    /**
     * 根据试卷id获取试卷信息
     *
     * @param paperId : 试卷id
     * @return : cn.gpnusz.ucloudteachentity.entity.ExamPaper
     * @author h0ss
     */
    ExamPaper getPaperById(Long paperId);

    /**
     * 获取试卷数量
     *
     * @return : java.lang.Long
     * @author h0ss
     */
    Long getPaperCount();

}
