package cn.gpnusz.examservice.service;

import cn.gpnusz.examinterface.service.CommonExamService;
import cn.gpnusz.examservice.entity.ExamPaperExample;
import cn.gpnusz.examservice.mapper.ExamPaperMapper;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.ExamPaper;
import cn.gpnusz.ucloudteachentity.entity.Subject;
import cn.gpnusz.ucloudteachentity.req.ExamPaperQueryReq;
import com.github.pagehelper.PageHelper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author h0ss
 * @description 获取试卷信息的通用业务层
 * @date 2021/12/1 - 2:09
 */
@DubboService(interfaceClass = CommonExamService.class, version = "1.0.0", timeout = 10000)
public class CommonExamServiceImpl implements CommonExamService {

    @Resource
    private ExamPaperMapper examPaperMapper;


    /**
     * 根据年级id获取对应试卷信息
     *
     * @param subjectList : 科目列表
     * @param size        : 每页数目
     * @param page        : 页码
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.ExamPaper>
     * @author h0ss
     */
    @Override
    public PageResp<ExamPaper> getPaperByGrade(List<Subject> subjectList, Integer size, Integer page) {
        if (subjectList.isEmpty()) {
            return null;
        }
        ArrayList<Long> subjectIds = new ArrayList<>(subjectList.size());
        for (Subject subject : subjectList) {
            subjectIds.add(subject.getId());
        }
        ExamPaperExample example = new ExamPaperExample();
        ExamPaperExample.Criteria criteria = example.createCriteria();
        criteria.andSubjectIdIn(subjectIds);
        // 查询已上架的
        criteria.andStatusEqualTo(Boolean.TRUE);
        // 只查询有效期内的试卷
        Date now = new Date();
        criteria.andStartDateLessThanOrEqualTo(now);
        criteria.andEndDateGreaterThanOrEqualTo(now);
        PageHelper.startPage(page, size);
        example.setOrderByClause("sort desc");
        return PageInfoUtil.getPageInfoResp(examPaperMapper.selectByExample(example), ExamPaper.class);
    }


    /**
     * 公共查询试卷信息的业务方法 只显示有效期内的试卷
     *
     * @param examPaperQueryReq : 查询信息
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.ExamPaper>
     * @author h0ss
     */
    @Override
    public PageResp<ExamPaper> getPaperCommon(ExamPaperQueryReq examPaperQueryReq) {
        ExamPaperExample examPaperExample = new ExamPaperExample();
        ExamPaperExample.Criteria criteria = examPaperExample.createCriteria();
        if (!ObjectUtils.isEmpty(examPaperQueryReq.getId())) {
            criteria.andIdEqualTo(examPaperQueryReq.getId());
        }
        // 只查询有效期内的试卷
        criteria.andStartDateLessThanOrEqualTo(new Date());
        criteria.andEndDateGreaterThanOrEqualTo(new Date());
        criteria.andStatusEqualTo(examPaperQueryReq.getStatus());
        // 分页查询
        if (examPaperQueryReq.getPage() != null && examPaperQueryReq.getSize() != null) {
            PageHelper.startPage(examPaperQueryReq.getPage(), examPaperQueryReq.getSize());
        }
        examPaperExample.setOrderByClause("sort desc");
        List<ExamPaper> examPaperList = examPaperMapper.selectByExample(examPaperExample);
        return PageInfoUtil.getPageInfoResp(examPaperList, ExamPaper.class);
    }

}
