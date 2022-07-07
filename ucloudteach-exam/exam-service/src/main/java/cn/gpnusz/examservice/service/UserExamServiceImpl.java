package cn.gpnusz.examservice.service;

import cn.gpnusz.examinterface.service.UserExamService;
import cn.gpnusz.examservice.entity.ExamInfoExample;
import cn.gpnusz.examservice.entity.ExamPaperExample;
import cn.gpnusz.examservice.mapper.ExamInfoMapper;
import cn.gpnusz.examservice.mapper.ExamPaperMapper;
import cn.gpnusz.examservice.mapper.UserExamCustMapper;
import cn.gpnusz.examservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.ExamInfo;
import cn.gpnusz.ucloudteachentity.entity.ExamPaper;
import cn.gpnusz.ucloudteachentity.req.UserExamSaveReq;
import cn.gpnusz.ucloudteachentity.resp.UserExamDetailResp;
import cn.gpnusz.ucloudteachentity.resp.UserExamResp;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * @author h0ss
 * @description 用户操作考试信息业务层
 * @date 2021/11/28 3:27
 */
@DubboService(interfaceClass = UserExamService.class, version = "1.0.0", timeout = 10000)
public class UserExamServiceImpl implements UserExamService {
    @Resource
    private UserExamCustMapper userExamCustMapper;

    @Resource
    private ExamInfoMapper examInfoMapper;

    @Resource
    private ExamPaperMapper examPaperMapper;

    @Resource
    private ExamPaperServiceImpl examPaperServiceImpl;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private AsyncServiceImpl asyncService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(UserExamServiceImpl.class);

    /**
     * 用户查询已参加考试信息的业务方法
     *
     * @param userId: 用户id
     * @return : java.util.List<cn.gpnusz.ucloudteach.resp.UserExamResp>
     * @author h0ss
     */
    @Override
    public List<UserExamResp> selectUserExam(Long userId) {
        return userExamCustMapper.selectExamInfo(userId);
    }

    /**
     * 用户查询考试结果的业务方法
     *
     * @param userId: 用户id
     * @param paperId : 试卷ID
     * @return : cn.gpnusz.ucloudteach.resp.UserExamResp
     * @author h0ss
     */
    @Override
    public UserExamResp getExamRes(Long userId, Long paperId) {
        return userExamCustMapper.getExamRes(userId, paperId);
    }

    /**
     * 获取考试的试题信息【不带答案】
     *
     * @param userId : 用户id
     * @param id     : 试卷id
     * @return : java.util.List<cn.gpnusz.ucloudteach.resp.UserExamDetailResp>
     * @author h0ss
     */
    @Override
    public List<UserExamDetailResp> getExamDetail(Long userId, Long id) {
        // 检验参与状态以及是否过期
        if (checkExamJoinStatus(userId, id) && checkValid(id)) {
            return userExamCustMapper.getExamDetail(id);
        } else {
            return null;
        }
    }

    /**
     * 获取练习的试题信息【带答案】
     *
     * @param id : 试卷id
     * @return : java.util.List<cn.gpnusz.ucloudteach.resp.UserExamDetailResp>
     * @author h0ss
     */
    @Override
    public List<UserExamDetailResp> getExerciseDetail(Long id) {
        if (checkValid(id)) {
            return userExamCustMapper.getExerciseDetail(id);
        } else {
            return null;
        }
    }

    /**
     * 检查试卷是否已经过期
     *
     * @param paperId : 试卷id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    @Override
    public Boolean checkValid(Long paperId) {
        // 判断试卷是否过期
        ExamPaperExample example = new ExamPaperExample();
        ExamPaperExample.Criteria criteria = example.createCriteria();
        Date now = new Date();
        criteria.andIdEqualTo(paperId);
        criteria.andStartDateLessThanOrEqualTo(now);
        criteria.andEndDateGreaterThanOrEqualTo(now);
        return examPaperMapper.countByExample(example) != 0;
    }

    /**
     * 检查是否参加过考试
     *
     * @param userId: 用户id
     * @param paperId : 试卷id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    @Override
    public Boolean checkExamJoinStatus(Long userId, Long paperId) {
        ExamInfoExample example = new ExamInfoExample();
        ExamInfoExample.Criteria criteria = example.createCriteria();
        criteria.andPaperIdEqualTo(paperId);
        criteria.andStudentIdEqualTo(userId);
        if (ObjectUtils.isEmpty(examInfoMapper.selectByExample(example))) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    /**
     * 学员参与考试的业务方法
     *
     * @param userId: 用户id
     * @param paperId : 试卷id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @Override
    public CommonResp<Object> joinExam(Long userId, Long paperId) {
        CommonResp<Object> resp = new CommonResp<>();
        Boolean status = checkExamJoinStatus(userId, paperId);
        if (status) {
            resp.setMessage("您已参加过本次考试");
            resp.setSuccess(false);
        } else {
            // 开始插入考试信息表
            ExamInfo examInfo = new ExamInfo();
            examInfo.setPaperId(paperId);
            examInfo.setStudentId(userId);
            examInfo.setCreateTime(new Date());
            examInfo.setId(snowFlake.nextId());
            examInfoMapper.insertSelective(examInfo);
            examPaperServiceImpl.plusJoinCount(paperId);
            LOG.info("学员 {} 参加了考试", userId);
            resp.setMessage("成功参加考试");
        }
        return resp;
    }

    /**
     * 学员保存考试答案的业务方法
     * 异步提交批阅业务
     *
     * @param userId          : 用户id
     * @param userExamSaveReq : 作答信息
     * @author h0ss
     */
    @Override
    public CommonResp<Object> submitExam(Long userId, UserExamSaveReq userExamSaveReq) {
        CommonResp<Object> resp = new CommonResp<>();
        // 创建一个考试信息对象
        ExamInfo examInfo = new ExamInfo();
        // 填充作答信息
        examInfo.setStudentAnswer(userExamSaveReq.getStudentAnswer());
        examInfo.setStatus(Boolean.TRUE);
        // 更新到数据库中 先查找是否已经提交过
        ExamInfoExample example = new ExamInfoExample();
        ExamInfoExample.Criteria criteria = example.createCriteria();
        // 因有唯一键限制 找到唯一记录
        criteria.andStudentIdEqualTo(userId);
        criteria.andPaperIdEqualTo(userExamSaveReq.getPaperId());
        if (!examInfoMapper.selectByExample(example).isEmpty()) {
            ExamInfo examInfoBySelect = examInfoMapper.selectByExample(example).get(0);
            if (!examInfoBySelect.getStatus()) {
                // 取出开始时间与当前时间做差值
                Date createTime = examInfoBySelect.getCreateTime();
                Integer spendTime = Math.toIntExact(Duration.between(createTime.toInstant(), Instant.now()).getSeconds());
                // 取出试卷限时 比较是否属于已超时自动提交
                ExamPaper paper = examPaperServiceImpl.getPaperById(examInfoBySelect.getPaperId());
                if (paper != null && spendTime.compareTo(paper.getExamTime()) > 0) {
                    spendTime = paper.getExamTime();
                }
                examInfo.setExamTime(spendTime);
                examInfoMapper.updateByExampleSelective(examInfo, example);
                resp.setMessage("保存成功，考试结束");
                // 考试信息异步批阅
                asyncService.checkService(examInfo.getId());
            } else {
                resp.setMessage("作答信息已提交");
                resp.setSuccess(false);
            }
        } else {
            resp.setMessage("查找不到对应考试信息");
            resp.setSuccess(false);
        }
        return resp;
    }


    /**
     * 检查考试提交状态的业务方法
     *
     * @param userId  : 用户id
     * @param paperId : 试卷id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    @Override
    public Boolean checkSubmit(Long userId, Long paperId) {
        ExamInfoExample example = new ExamInfoExample();
        ExamInfoExample.Criteria criteria = example.createCriteria();
        criteria.andStudentIdEqualTo(userId);
        criteria.andPaperIdEqualTo(paperId);
        if (!examInfoMapper.selectByExample(example).isEmpty()) {
            return examInfoMapper.selectByExample(example).get(0).getStatus();
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 获取考试剩余时间
     *
     * @param userId: 用户id
     * @param paperId : 试卷id
     * @return : java.lang.Integer
     * @author h0ss
     */
    @Override
    public Integer getSurplusTime(Long userId, Long paperId) {
        // 获取对应考试信息
        ExamInfoExample example = new ExamInfoExample();
        ExamInfoExample.Criteria criteria = example.createCriteria();
        criteria.andStudentIdEqualTo(userId);
        criteria.andPaperIdEqualTo(paperId);
        List<ExamInfo> data = examInfoMapper.selectByExample(example);
        // 查找出试卷信息
        ExamPaper paper = examPaperServiceImpl.getPaperById(paperId);
        if (!data.isEmpty() && paper != null) {
            // 取出开始时间与当前时间做差值
            Date createTime = data.get(0).getCreateTime();
            Integer spendTime = Math.toIntExact(Duration.between(createTime.toInstant(), Instant.now()).getSeconds());
            // 计算剩余时间
            Integer totalTime = paper.getExamTime();
            int surplus = totalTime - spendTime;
            if (surplus <= 0) {
                // 超时设置为提交状态
                ExamInfo examInfo = new ExamInfo();
                examInfo.setStatus(Boolean.TRUE);
                // 从redis中取出数据 填充到答卷中
                examInfo.setStudentAnswer(loadAnswer(userId, paperId).getContent());
                examInfoMapper.updateByExampleSelective(examInfo, example);
            }
            return surplus;
        } else {
            return 0;
        }
    }

    /**
     * 暂存答卷信息
     *
     * @param userId          : 用户id
     * @param userExamSaveReq : 答卷信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @Override
    public CommonResp<Object> saveAnswer(Long userId, UserExamSaveReq userExamSaveReq) {
        CommonResp<Object> resp = new CommonResp<>();
        if (userExamSaveReq != null) {
            String key = RedisKeyUtil.getExamStoreKey(Long.toString(userId), Long.toString(userExamSaveReq.getPaperId()));
            stringRedisTemplate.opsForValue().set(key, userExamSaveReq.getStudentAnswer());
            resp.setMessage("保存成功");
            return resp;
        }
        resp.setMessage("保存失败");
        resp.setSuccess(false);
        return resp;
    }

    /**
     * 获取暂存答案
     *
     * @param userId  : 用户id
     * @param paperId : 试卷id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    @Override
    public CommonResp<String> loadAnswer(Long userId, Long paperId) {
        CommonResp<String> resp = new CommonResp<>();
        String key = RedisKeyUtil.getExamStoreKey(Long.toString(userId), Long.toString(paperId));
        String answer = stringRedisTemplate.opsForValue().get(key);
        if (answer != null) {
            resp.setContent(answer);
            resp.setMessage("暂存答案获取成功");
        } else {
            resp.setSuccess(false);
            resp.setMessage("无暂存答案");
        }
        return resp;
    }
}
