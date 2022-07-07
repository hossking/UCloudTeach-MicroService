package cn.gpnusz.customservice.service;

import cn.gpnusz.courseinterface.service.CourseService;
import cn.gpnusz.customservice.entity.SnapshotExample;
import cn.gpnusz.customservice.mapper.SnapshotMapper;
import cn.gpnusz.customservice.util.SnowFlake;
import cn.gpnusz.custominterface.service.SnapshotService;
import cn.gpnusz.examinterface.service.ExamPaperService;
import cn.gpnusz.examinterface.service.QuestionService;
import cn.gpnusz.ucloudteachentity.entity.Snapshot;
import cn.gpnusz.userinterface.service.StudentInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author h0ss
 * @description 快照数据业务层
 * @date 2021/12/4 - 18:06
 */
@DubboService(version = "1.0.0", timeout = 10000, interfaceClass = SnapshotService.class)
public class SnapshotServiceImpl implements SnapshotService {

    @Resource
    private SnapshotMapper snapshotMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SnowFlake snowFlake;

    @DubboReference(version = "1.0.0")
    private CourseService courseService;

    @DubboReference(version = "1.0.0")
    private StudentInfoService studentInfoService;

    @DubboReference(version = "1.0.0")
    private ExamPaperService examPaperService;

    @DubboReference(version = "1.0.0")
    private QuestionService questionService;


    /**
     * 生成平台数据快照
     *
     * @author h0ss
     */
    @Override
    public void generateSnapshot() {
        Snapshot snapshot = new Snapshot();
        // 获取信息 通过远程调用方法获取
        long courseCount = courseService.getCourseCount();
        long studentCount = studentInfoService.getStuCount();
        long paperCount = examPaperService.getPaperCount();
        long questionCount = questionService.getQuestionCount();
        // 写入实体
        snapshot.setCourseCount((int) courseCount);
        snapshot.setStudentCount((int) studentCount);
        snapshot.setPaperCount((int) paperCount);
        snapshot.setQuestionCount((int) questionCount);
        snapshot.setCreateDate(new Date());
        snapshot.setId(snowFlake.nextId());
        snapshotMapper.insert(snapshot);
        stringRedisTemplate.opsForValue().set("snapshotData", JSON.toJSONString(snapshot), 1, TimeUnit.DAYS);
    }

    /**
     * 获取快照数据的业务方法
     *
     * @return : cn.gpnusz.ucloudteach.entity.Snapshot
     * @author h0ss
     */
    @Override
    public Snapshot getData() {
        // 从Redis中取数据
        String data = stringRedisTemplate.opsForValue().get("snapshotData");
        if (data != null) {
            JSONObject obj = JSON.parseObject(data);
            if (!obj.isEmpty()) {
                Snapshot snapshot = new Snapshot();
                snapshot.setId(obj.getLong("id"));
                snapshot.setQuestionCount(obj.getInteger("questionCount"));
                snapshot.setPaperCount(obj.getInteger("paperCount"));
                snapshot.setStudentCount(obj.getInteger("studentCount"));
                snapshot.setCreateDate(obj.getDate("createDate"));
                snapshot.setCourseCount(obj.getInteger("courseCount"));
                return snapshot;
            }
        }
        // 从数据库获取快照数据
        SnapshotExample example = new SnapshotExample();
        SnapshotExample.Criteria criteria = example.createCriteria();
        criteria.andCreateDateEqualTo(new Date());
        List<Snapshot> snapshots = snapshotMapper.selectByExample(example);
        if (!snapshots.isEmpty()) {
            stringRedisTemplate.opsForValue().set("snapshotData", JSON.toJSONString(snapshots.get(0)), 1, TimeUnit.DAYS);
            return snapshots.get(0);
        }
        return null;
    }

    /**
     * 获取前七天的数据
     *
     * @return : java.util.List<cn.gpnusz.ucloudteach.entity.Snapshot>
     * @author h0ss
     */
    @Override
    public List<Snapshot> getBeforeData() {
        SnapshotExample example = new SnapshotExample();
        example.setOrderByClause("create_date desc");
        PageHelper.startPage(1, 15);
        return snapshotMapper.selectByExample(example);
    }
}
