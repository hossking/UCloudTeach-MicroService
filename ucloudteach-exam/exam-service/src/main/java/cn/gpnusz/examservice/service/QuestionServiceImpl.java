package cn.gpnusz.examservice.service;


import cn.gpnusz.examinterface.service.QuestionService;
import cn.gpnusz.examservice.entity.QuestionExample;
import cn.gpnusz.examservice.mapper.QuestionMapper;
import cn.gpnusz.examservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.Question;
import cn.gpnusz.ucloudteachentity.req.QuestionQueryReq;
import cn.gpnusz.ucloudteachentity.req.QuestionSaveReq;
import cn.gpnusz.ucloudteachentity.resp.QuestionAnswerResp;
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
 * @description 操作题目信息的业务层
 * @date 2021/11/18 17:30
 */

@DubboService(interfaceClass = QuestionService.class, version = "1.0.0", timeout = 10000)
public class QuestionServiceImpl implements QuestionService {
    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private SnowFlake snowFlake;

    private static final Logger LOG = LoggerFactory.getLogger(QuestionServiceImpl.class);

    /**
     * 按传入的条件查询题目
     *
     * @param questionQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.Question>
     * @author h0ss
     */
    @Override
    public PageResp<Question> getAllByCondition(QuestionQueryReq questionQueryReq) {
        QuestionExample questionExample = new QuestionExample();
        QuestionExample.Criteria criteria = questionExample.createCriteria();
        // 查询条件判断
        if (!ObjectUtils.isEmpty(questionQueryReq.getId())) {
            criteria.andIdEqualTo(questionQueryReq.getId());
        }
        if (!ObjectUtils.isEmpty(questionQueryReq.getSubjectId())) {
            criteria.andSubjectIdEqualTo(questionQueryReq.getSubjectId());
        }
        if (!ObjectUtils.isEmpty(questionQueryReq.getCourseId())) {
            criteria.andCourseIdEqualTo(questionQueryReq.getCourseId());
        }
        if (!ObjectUtils.isEmpty(questionQueryReq.getSectionId())) {
            criteria.andSectionIdEqualTo(questionQueryReq.getSectionId());
        }
        if (!ObjectUtils.isEmpty(questionQueryReq.getType())) {
            criteria.andTypeEqualTo(questionQueryReq.getType());
        }
        // 获取全部题目信息,不设置则每次只显示前100条
        if (questionQueryReq.getPage() != null && questionQueryReq.getSize() != null) {
            PageHelper.startPage(questionQueryReq.getPage(), questionQueryReq.getSize());
        } else {
            PageHelper.startPage(1, 100);
        }
        questionExample.setOrderByClause("create_time desc");
        List<Question> questionList = questionMapper.selectByExampleWithBLOBs(questionExample);
        return PageInfoUtil.getPageInfoResp(questionList, Question.class);
    }

    /**
     * 新增或编辑题目信息的业务方法
     *
     * @param questionSaveReq : 保存的题目信息数据
     * @author h0ss
     */
    @Override
    public void save(QuestionSaveReq questionSaveReq) {
        // 创建一个新对象
        Question question = new Question();
        BeanUtils.copyProperties(questionSaveReq, question);
        // 判断是新增还是编辑
        if (question.getId() != null) {
            QuestionExample questionExample = new QuestionExample();
            QuestionExample.Criteria criteria = questionExample.createCriteria();
            criteria.andIdEqualTo(question.getId());
            questionMapper.updateByExampleSelective(question, questionExample);
        } else {
            // 设置创建日期时间
            question.setCreateTime(new Date());
            // 雪花算法生成id
            question.setId(snowFlake.nextId());
            questionMapper.insert(question);
        }
    }

    /**
     * 删除题目信息的业务方法
     *
     * @param id : 要删除的题目信息id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        QuestionExample questionExample = new QuestionExample();
        QuestionExample.Criteria criteria = questionExample.createCriteria();
        criteria.andIdEqualTo(id);
        questionMapper.deleteByExample(questionExample);
    }


    /**
     * 获取题目答案的业务方法
     *
     * @param id : 题目id
     * @return : cn.gpnusz.ucloudteachentity.resp.QuestionAnswerResp
     * @author h0ss
     */
    @Override
    public QuestionAnswerResp getAnswer(Long id) {
        QuestionExample example = new QuestionExample();
        QuestionExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Question> res = questionMapper.selectByExampleWithBLOBs(example);
        if (!res.isEmpty()) {
            QuestionAnswerResp resp = new QuestionAnswerResp();
            BeanUtils.copyProperties(res.get(0), resp);
            return resp;
        }
        return null;
    }

    /**
     * 获取试题数量
     *
     * @return : java.lang.Long
     * @author h0ss
     */
    @Override
    public Long getQuestionCount() {
        return questionMapper.countByExample(null);
    }
}
