package cn.gpnusz.examservice.service;

import cn.gpnusz.examinterface.service.ExamPaperService;
import cn.gpnusz.examservice.entity.ExamPaperExample;
import cn.gpnusz.examservice.entity.PaperQuestionExample;
import cn.gpnusz.examservice.mapper.ExamPaperMapper;
import cn.gpnusz.examservice.mapper.PaperQuestionCustMapper;
import cn.gpnusz.examservice.mapper.PaperQuestionMapper;
import cn.gpnusz.examservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.ExamPaper;
import cn.gpnusz.ucloudteachentity.entity.PaperQuestion;
import cn.gpnusz.ucloudteachentity.req.ExamPaperQueryReq;
import cn.gpnusz.ucloudteachentity.req.ExamPaperSaveReq;
import cn.gpnusz.ucloudteachentity.req.PaperQuestionSaveReq;
import cn.gpnusz.ucloudteachentity.resp.QuestionCust;
import com.github.pagehelper.PageHelper;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author h0ss
 * @description 操作试卷信息的业务层
 * @date 2021/11/19 19:25
 */

@DubboService(interfaceClass = ExamPaperService.class, version = "1.0.0", timeout = 10000)
public class ExamPaperServiceImpl implements ExamPaperService {
    @Resource
    private ExamPaperMapper examPaperMapper;

    @Resource
    private PaperQuestionMapper paperQuestionMapper;

    @Resource
    private PaperQuestionCustMapper paperQuestionCustMapper;

    @Resource
    private SnowFlake snowFlake;

    private static final Logger LOG = LoggerFactory.getLogger(ExamPaperServiceImpl.class);

    /**
     * 按传入的条件查询试卷
     *
     * @param examPaperQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.ExamPaper>
     * @author h0ss
     */
    @Override
    public PageResp<ExamPaper> getAllByCondition(ExamPaperQueryReq examPaperQueryReq) {
        ExamPaperExample examPaperExample = new ExamPaperExample();
        ExamPaperExample.Criteria criteria = examPaperExample.createCriteria();
        // 查询条件判断
        if (!ObjectUtils.isEmpty(examPaperQueryReq.getId())) {
            criteria.andIdEqualTo(examPaperQueryReq.getId());
        }
        if (!ObjectUtils.isEmpty(examPaperQueryReq.getCourseId())) {
            criteria.andCourseIdEqualTo(examPaperQueryReq.getCourseId());
        }
        if (!ObjectUtils.isEmpty(examPaperQueryReq.getSubjectId())) {
            criteria.andSubjectIdEqualTo(examPaperQueryReq.getSubjectId());
        }
        if (!ObjectUtils.isEmpty(examPaperQueryReq.getName())) {
            criteria.andNameLike("%" + examPaperQueryReq.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(examPaperQueryReq.getStatus())) {
            criteria.andStatusEqualTo(examPaperQueryReq.getStatus());
        }
        // 分页查询
        if (examPaperQueryReq.getPage() != null && examPaperQueryReq.getSize() != null) {
            PageHelper.startPage(examPaperQueryReq.getPage(), examPaperQueryReq.getSize());
        }
        examPaperExample.setOrderByClause("sort desc,create_time desc");
        List<ExamPaper> examPaperList = examPaperMapper.selectByExample(examPaperExample);
        return PageInfoUtil.getPageInfoResp(examPaperList, ExamPaper.class);
    }

    /**
     * 新增或编辑试卷信息的业务方法
     *
     * @param examPaperSaveReq : 保存的试卷信息数据
     * @author h0ss
     */
    @Override
    public void save(ExamPaperSaveReq examPaperSaveReq) {
        // 创建一个新对象
        ExamPaper examPaper = new ExamPaper();
        BeanUtils.copyProperties(examPaperSaveReq, examPaper);
        // 判断是新增还是编辑
        if (examPaper.getId() != null) {
            ExamPaperExample examPaperExample = new ExamPaperExample();
            ExamPaperExample.Criteria criteria = examPaperExample.createCriteria();
            criteria.andIdEqualTo(examPaper.getId());
            // 设置为发布状态之后自动计算下属题目的总分
            if (examPaper.getStatus()) {
                int totalScore = 0;
                List<QuestionCust> list = paperQuestionCustMapper.selectQuestionByPaperId(examPaper.getId());
                for (QuestionCust questionCust : list) {
                    totalScore += questionCust.getScore();
                }
                examPaper.setTotalScore(totalScore);
            }
            examPaperMapper.updateByExampleSelective(examPaper, examPaperExample);
        } else {
            // 设置创建日期时间
            examPaper.setCreateTime(new Date());
            // 默认值
            examPaper.setJoinCount(0);
            examPaper.setCheckCount(0);
            examPaper.setQuestionCount(0);
            // 雪花算法生成id
            examPaper.setId(snowFlake.nextId());
            examPaperMapper.insertSelective(examPaper);
        }
    }

    /**
     * 删除试卷信息的业务方法
     *
     * @param id : 要删除的试卷信息id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        ExamPaperExample examPaperExample = new ExamPaperExample();
        ExamPaperExample.Criteria criteria = examPaperExample.createCriteria();
        criteria.andIdEqualTo(id);
        examPaperMapper.deleteByExample(examPaperExample);
    }


    /**
     * 考试人数增长的业务方法
     *
     * @param id : 试卷id
     * @author h0ss
     */
    @Override
    public void plusJoinCount(Long id) {
        ExamPaperExample examPaperExample = new ExamPaperExample();
        ExamPaperExample.Criteria criteria = examPaperExample.createCriteria();
        criteria.andIdEqualTo(id);
        List<ExamPaper> papers = examPaperMapper.selectByExample(examPaperExample);
        if (!ObjectUtils.isEmpty(papers)) {
            ExamPaper paper = papers.get(0);
            // 这里需要分布式锁解决[0407]
            paper.setJoinCount(paper.getJoinCount() + 1);
            examPaperMapper.updateByExampleSelective(paper, examPaperExample);
        }
    }

    /**
     * 批阅数增长的业务方法
     *
     * @param paperId : 试卷id
     * @author h0ss
     */
    @Override
    public void plusCheckCount(Long paperId) {
        ExamPaperExample examPaperExample = new ExamPaperExample();
        ExamPaperExample.Criteria criteria = examPaperExample.createCriteria();
        criteria.andIdEqualTo(paperId);
        List<ExamPaper> papers = examPaperMapper.selectByExample(examPaperExample);
        if (!ObjectUtils.isEmpty(papers)) {
            ExamPaper paper = papers.get(0);
            // 这里需要分布式锁解决[0407]
            paper.setCheckCount(paper.getCheckCount() + 1);
            examPaperMapper.updateByExampleSelective(paper, examPaperExample);
        }
    }

    /**
     * 查询试卷对应的题目的业务方法
     *
     * @param paperId : 试卷id
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.resp.QuestionCust>
     * @author h0ss
     */
    @Override
    public List<QuestionCust> getQuestions(Long paperId) {
        return paperQuestionCustMapper.selectQuestionByPaperId(paperId);
    }

    /**
     * 保存试卷-题目关联信息的业务方法
     *
     * @param paperQuestionSaveReq : 试卷-题目关联信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @Override
    public CommonResp<Object> savePaperAndQuestion(PaperQuestionSaveReq paperQuestionSaveReq) {
        CommonResp<Object> resp = new CommonResp<>();
        resp.setSuccess(false);
        if (paperQuestionSaveReq == null) {
            resp.setMessage("保存失败");
            return resp;
        }
        // 上架状态不可挑选题目
        ExamPaper paper = getPaperById(paperQuestionSaveReq.getPaperId());
        if (paper == null || paper.getStatus()) {
            resp.setMessage("上架状态无法添加题目");
            return resp;
        }
        PaperQuestion paperQuestion = new PaperQuestion();
        BeanUtils.copyProperties(paperQuestionSaveReq, paperQuestion);
        PaperQuestionExample paperQuestionExample = new PaperQuestionExample();
        PaperQuestionExample.Criteria criteria = paperQuestionExample.createCriteria();
        // 判断新增还是修改
        if (paperQuestion.getId() != null) {
            criteria.andIdEqualTo(paperQuestion.getId());
            paperQuestionMapper.updateByExampleSelective(paperQuestion, paperQuestionExample);
        } else {
            // 设置创建日期时间
            paperQuestion.setCreateTime(new Date());
            // 自动设置题号
            criteria.andPaperIdEqualTo(paperQuestion.getPaperId());
            paperQuestion.setSort((int) (paperQuestionMapper.countByExample(paperQuestionExample) + 1));
            // 雪花算法生成id
            paperQuestion.setId(snowFlake.nextId());
            paperQuestionMapper.insert(paperQuestion);
        }
        updateQuestionCount(paperQuestion.getPaperId());
        resp.setSuccess(true);
        return resp;
    }

    /**
     * 从试卷中删除题目的业务方法
     *
     * @param id : 要删除的试卷-题目关联信息id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @Override
    public CommonResp<Object> deletePaperAndQuestion(Long id) {
        CommonResp<Object> resp = new CommonResp<>();
        resp.setSuccess(false);

        PaperQuestionExample paperQuestionExample = new PaperQuestionExample();
        PaperQuestionExample.Criteria criteria = paperQuestionExample.createCriteria();
        criteria.andIdEqualTo(id);
        // 如果id对应项存在则更新试卷中题目数信息
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectByExample(paperQuestionExample);
        if (!ObjectUtils.isEmpty(paperQuestions)) {
            // 上架状态不可操作题目
            ExamPaper paper = getPaperById(paperQuestions.get(0).getPaperId());
            if (paper == null || paper.getStatus()) {
                resp.setMessage("上架状态无法添加题目");
                return resp;
            }
            paperQuestionMapper.deleteByExample(paperQuestionExample);
            updateQuestionCount(paperQuestions.get(0).getPaperId());
        }
        resp.setSuccess(true);
        return resp;
    }

    /**
     * 更新试卷中的试题数
     * 查看更新频度 频繁就不应该在db操作[0407]
     *
     * @param paperId : 试卷id
     * @author h0ss
     */
    @Override
    public void updateQuestionCount(Long paperId) {
        // 对试卷的试题数进行更新操作
        ExamPaperExample examPaperExample = new ExamPaperExample();
        ExamPaperExample.Criteria examPaperExampleCriteria = examPaperExample.createCriteria();
        PaperQuestionExample paperQuestionExample = new PaperQuestionExample();
        PaperQuestionExample.Criteria criteria = paperQuestionExample.createCriteria();
        examPaperExampleCriteria.andIdEqualTo(paperId);
        List<ExamPaper> papers = examPaperMapper.selectByExample(examPaperExample);
        if (!ObjectUtils.isEmpty(papers)) {
            criteria.andPaperIdEqualTo(paperId);
            papers.get(0).setQuestionCount((int) paperQuestionMapper.countByExample(paperQuestionExample));
            examPaperMapper.updateByExampleSelective(papers.get(0), examPaperExample);
        }
    }

    /**
     * 根据试卷id获取试卷信息
     *
     * @param paperId : 试卷id
     * @return : cn.gpnusz.ucloudteachentity.entity.ExamPaper
     * @author h0ss
     */
    @Override
    public ExamPaper getPaperById(Long paperId) {
        ExamPaperExample example = new ExamPaperExample();
        ExamPaperExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(paperId);
        List<ExamPaper> paperList = examPaperMapper.selectByExample(example);
        if (paperList != null && !paperList.isEmpty()) {
            return paperList.get(0);
        }
        return null;
    }

    /**
     * 获取试卷数量
     *
     * @return : java.lang.Long
     * @author h0ss
     */
    @Override
    public Long getPaperCount() {
        return examPaperMapper.countByExample(null);
    }

}
