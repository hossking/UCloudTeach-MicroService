package cn.gpnusz.examservice.service;

import cn.gpnusz.examinterface.service.ExamCheckService;
import cn.gpnusz.examservice.entity.ExamInfoExample;
import cn.gpnusz.examservice.entity.ExamPaperExample;
import cn.gpnusz.examservice.mapper.ExamInfoMapper;
import cn.gpnusz.examservice.mapper.ExamPaperMapper;
import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.ExamInfo;
import cn.gpnusz.ucloudteachentity.req.ExamCheck;
import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author h0ss
 * @description 批阅试卷服务方法
 * @date 2022/3/23 - 19:47
 */
@DubboService(interfaceClass = ExamCheckService.class, version = "1.0.0", timeout = 10000)
public class ExamCheckServiceImpl implements ExamCheckService {

    @Resource
    private ExamPaperMapper examPaperMapper;

    @Resource
    private ExamInfoMapper examInfoMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 检查是否需要自动批阅
     *
     * @param paperId : 试卷id
     * @return : boolean
     * @author h0ss
     */
    @Override
    public boolean needCheck(Long paperId) {
        ExamPaperExample epe = new ExamPaperExample();
        ExamPaperExample.Criteria epeCriteria = epe.createCriteria();
        epeCriteria.andIdEqualTo(paperId).andAutoCheckEqualTo(Boolean.TRUE);
        long needCheck = examPaperMapper.countByExample(epe);
        return needCheck > 0;
    }

    /**
     * 手动批阅题目
     *
     * @param userId       : 用户id
     * @param examCheckReq : 批阅信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @Override
    public CommonResp<Object> submitCheck(Long userId, ExamCheck examCheckReq) {
        CommonResp<Object> resp = new CommonResp<>();
        if (examCheckReq == null) {
            resp.setMessage("保存失败，批阅信息缺失");
            resp.setSuccess(false);
            return resp;
        }
        ExamInfoExample eie = new ExamInfoExample();
        ExamInfoExample.Criteria eieCriteria = eie.createCriteria();
        eieCriteria.andIdEqualTo(examCheckReq.getId());
        List<ExamInfo> examInfos = examInfoMapper.selectByExampleWithBLOBs(eie);
        if (examInfos == null || examInfos.isEmpty()) {
            resp.setMessage("保存失败，试卷信息不存在");
            resp.setSuccess(false);
            return resp;
        }
        // 取出考试信息
        ExamInfo examInfo = examInfos.get(0);
        // 开始写入批阅信息
        examInfo.setCheckFlag(Boolean.TRUE);
        examInfo.setCheckId(userId);
        examInfo.setScore(examCheckReq.getScore());
        examInfo.setRightCount(examCheckReq.getRightCount());
        examInfo.setErrorCount(examCheckReq.getErrorCount());
        examInfo.setCheckList(examCheckReq.getCheckList());
        examInfoMapper.updateByExampleSelective(examInfo, eie);
        // 走到这里说明成功保存了
        resp.setSuccess(true);
        resp.setMessage("保存成功");
        return resp;
    }

    /**
     * 暂存批阅信息
     *
     * @param userId       : 用户id
     * @param examCheckReq : 批阅信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @Override
    public CommonResp<Object> storeCheck(Long userId, ExamCheck examCheckReq) {
        CommonResp<Object> resp = new CommonResp<>();
        if (examCheckReq == null) {
            resp.setMessage("保存失败，批阅信息缺失");
            resp.setSuccess(false);
            return resp;
        }
        String key = RedisKeyUtil.getCheckStoreKey(Long.toString(userId), Long.toString(examCheckReq.getId()));
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(examCheckReq));
        resp.setSuccess(true);
        resp.setMessage("保存成功");
        return resp;
    }

    /**
     * 加载缓存批阅信息
     *
     * @param userId : 用户id
     * @param examId : 考试id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.req.ExamCheck>
     * @author h0ss
     */
    @Override
    public CommonResp<ExamCheck> loadCheck(Long userId, Long examId) {
        CommonResp<ExamCheck> resp = new CommonResp<>();
        String key = RedisKeyUtil.getCheckStoreKey(Long.toString(userId), Long.toString(examId));
        String entity = stringRedisTemplate.opsForValue().get(key);
        ExamCheck examCheck = JSON.parseObject(entity, ExamCheck.class);
        if (examCheck != null) {
            resp.setContent(examCheck);
            resp.setMessage("缓存数据读取成功");
            return resp;
        }
        resp.setSuccess(false);
        resp.setMessage("无缓存批阅数据");
        return resp;
    }
}
