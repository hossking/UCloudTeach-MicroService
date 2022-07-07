package cn.gpnusz.examinterface.service;


import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.ExamPaper;
import cn.gpnusz.ucloudteachentity.entity.Subject;
import cn.gpnusz.ucloudteachentity.req.ExamPaperQueryReq;

import java.util.ArrayList;
import java.util.List;

/**
 * @author h0ss
 * @description 获取试卷信息的通用业务层
 * @date 2021/12/1 - 2:09
 */
public interface CommonExamService {

    /**
     * 根据年级id获取对应试卷信息
     *
     * @param subjectList : 科目列表
     * @param size        : 每页数目
     * @param page        : 页码
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.ExamPaper>
     * @author h0ss
     */
    PageResp<ExamPaper> getPaperByGrade(List<Subject> subjectList, Integer size, Integer page);


    /**
     * 公共查询试卷信息的业务方法 只显示有效期内的试卷
     *
     * @param examPaperQueryReq : 查询信息
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.ExamPaper>
     * @author h0ss
     */
    PageResp<ExamPaper> getPaperCommon(ExamPaperQueryReq examPaperQueryReq);

}
