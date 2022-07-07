package cn.gpnusz.examservice.service;

import cn.gpnusz.examservice.entity.ExamInfoExample;
import cn.gpnusz.examservice.mapper.ExamInfoMapper;
import cn.gpnusz.examservice.mapper.PaperQuestionCustMapper;
import cn.gpnusz.ucloudteachentity.entity.ExamInfo;
import cn.gpnusz.ucloudteachentity.resp.QuestionCust;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author h0ss
 * @description 异步执行的试卷批阅业务
 * @date 2021/12/3 - 17:09
 */
@Service
@EnableAsync
public class AsyncServiceImpl {

    @Resource
    private ExamInfoMapper examInfoMapper;

    @Resource
    private PaperQuestionCustMapper paperQuestionCustMapper;

    @Resource
    private ExamCheckServiceImpl examCheckServiceImpl;

    @Resource
    private ExamPaperServiceImpl examPaperServiceImpl;

    @Resource(name = "ExamCheckThreadPool")
    private ThreadPoolExecutor threadPoolExecutor;

    private static final Logger LOG = LoggerFactory.getLogger(AsyncServiceImpl.class);


    @Async
    public void checkService(Long recordId) {
        // 根据考试信息id 查询出考试的信息
        ExamInfoExample eie = new ExamInfoExample();
        ExamInfoExample.Criteria eieCriteria = eie.createCriteria();
        // 需要对状态进行筛选 只筛选出未批阅的
        eieCriteria.andIdEqualTo(recordId).andStatusEqualTo(Boolean.FALSE);
        List<ExamInfo> examInfos = examInfoMapper.selectByExampleWithBLOBs(eie);
        // 说明被批阅完毕了 无需处理
        if (ObjectUtils.isEmpty(examInfos)) {
            return;
        }
        // 取出考试信息
        ExamInfo examInfo = examInfos.get(0);
        // 检查是否需要自动批阅 不需要自动批阅直接退出即可
        if (examCheckServiceImpl.needCheck(examInfo.getPaperId())) {
            return;
        }
        // 多线程批阅试卷
        // 查询试卷中试题对应的答案以及分值
        List<QuestionCust> questions = paperQuestionCustMapper.selectQuestionByPaperId(examInfo.getPaperId());
        // 初始化批阅总分 正确题数 错误题数
        AtomicInteger totalScore = new AtomicInteger();
        LongAdder rightCount = new LongAdder();
        LongAdder errorScore = new LongAdder();
        // 存放正确答案的map
        ConcurrentHashMap<Long, String> correctMap = new ConcurrentHashMap<>(questions.size());
        // 存放批阅得分细则的map 由于后续需要转成json 故键值为String
        ConcurrentHashMap<String, Integer> checkMap = new ConcurrentHashMap<>(questions.size());
        // 存放题目分值的map
        ConcurrentHashMap<String, Integer> scoreMap = new ConcurrentHashMap<>(questions.size());
        // 用于等待多线程获取结果的并发工具
        CountDownLatch waitResult = new CountDownLatch(questions.size());
        // 取出问题的正确答案 批阅得分细则默认为题目分数
        for (QuestionCust question : questions) {
            CountDownLatch waitQuestion = waitResult;
            threadPoolExecutor.execute(() -> {
                // 业务逻辑 判断题目类型
                if (question.getType() == 2 || question.getType() == 3) {
                    correctMap.put(question.getQuestionId(), question.getAnswerText());
                } else {
                    correctMap.put(question.getQuestionId(), question.getAnswerOption());
                }
                scoreMap.put(Long.toString(question.getQuestionId()), question.getScore());
                waitQuestion.countDown();
            });
        }
        try {
            waitResult.await();
        } catch (InterruptedException e) {
            LOG.info("多线程批阅发生中断，具体原因为 {}", e.getMessage());
            return;
        }
        // 开始校验对错
        String studentAnswer = examInfo.getStudentAnswer();
        JSONObject studentAnswerJson = JSON.parseObject(studentAnswer);
        // 用户答案不为空才去校对
        if (!studentAnswerJson.isEmpty()) {
            // 遍历答案去比较对错
            waitResult = new CountDownLatch(studentAnswerJson.size());
            for (Map.Entry<String, Object> set : studentAnswerJson.entrySet()) {
                CountDownLatch waitAnswerCheck = waitResult;
                threadPoolExecutor.execute(() -> {
                    String val1 = set.getValue().toString();
                    String val2 = correctMap.get(Long.valueOf(set.getKey()));
                    // 格式化后匹配
                    val1 = val1.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
                    // 正确与否判断
                    if (!val1.equals(val2)) {
                        checkMap.put(set.getKey(), 0);
                        errorScore.increment();
                    } else {
                        totalScore.addAndGet(scoreMap.get(set.getKey()));
                        rightCount.increment();
                        checkMap.put(set.getKey(), scoreMap.get(set.getKey()));
                    }
                    waitAnswerCheck.countDown();
                });
            }
            try {
                waitResult.await();
            } catch (InterruptedException e) {
                LOG.info("多线程批阅发生中断，具体原因为 {}", e.getMessage());
                return;
            }
        }
        // 开始写入批阅信息
        examInfo.setCheckFlag(Boolean.TRUE);
        examInfo.setScore(totalScore.get());
        examInfo.setRightCount(rightCount.intValue());
        examInfo.setErrorCount(errorScore.intValue());
        examInfo.setCheckList(JSON.toJSONString(checkMap));
        examInfoMapper.updateByExampleSelective(examInfo, eie);
        // 批阅数调整[利用Redis自增]
        examPaperServiceImpl.plusCheckCount(examInfo.getPaperId());
        LOG.info("[ASYNC] 学员 {} 的试卷 {} 批阅完成", examInfo.getStudentId(), examInfo.getPaperId());
    }
}
