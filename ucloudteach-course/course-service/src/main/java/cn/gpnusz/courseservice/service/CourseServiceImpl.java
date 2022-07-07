package cn.gpnusz.courseservice.service;


import cn.gpnusz.courseinterface.service.CourseService;
import cn.gpnusz.courseservice.entity.CourseExample;
import cn.gpnusz.courseservice.mapper.CourseCustMapper;
import cn.gpnusz.courseservice.mapper.CourseMapper;
import cn.gpnusz.courseservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
import cn.gpnusz.ucloudteachcommon.util.RedisOpsUtil;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.Course;
import cn.gpnusz.ucloudteachentity.req.CourseCommonReq;
import cn.gpnusz.ucloudteachentity.req.CourseQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseSaveReq;
import cn.gpnusz.ucloudteachentity.resp.CourseCustResp;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author h0ss
 * @description 课程信息业务层
 * @date 2021/11/14 20:09
 */

@Service
@DubboService(interfaceClass = CourseService.class, version = "1.0.0", timeout = 10000)
public class CourseServiceImpl implements CourseService {
    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CourseCustMapper courseCustMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "CourseDetailThreadPool")
    private ThreadPoolExecutor threadPoolExecutor;

    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final String col1 = "member";
    private final String col2 = "section";
    private final String col3 = "period";
    private List<Course> courseList;

    /**
     * 按传入条件查询课程信息的业务方法
     *
     * @param courseQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.Course>
     * @author h0ss
     */
    @Override
    public PageResp<Course> getAllByCondition(CourseQueryReq courseQueryReq) {
        CourseExample courseExample = new CourseExample();
        CourseExample.Criteria criteria = courseExample.createCriteria();
        courseExample.setOrderByClause("sort");
        if (!ObjectUtils.isEmpty(courseQueryReq.getName())) {
            criteria.andNameLike("%" + courseQueryReq.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(courseQueryReq.getTeacher())) {
            criteria.andTeacherLike("%" + courseQueryReq.getTeacher() + "%");
        }
        if (!ObjectUtils.isEmpty(courseQueryReq.getStatus())) {
            criteria.andStatusEqualTo(courseQueryReq.getStatus());
        }
        if (!ObjectUtils.isEmpty(courseQueryReq.getType())) {
            criteria.andTypeEqualTo(courseQueryReq.getType());
        }
        if (courseQueryReq.getPage() != null && courseQueryReq.getSize() != null) {
            PageHelper.startPage(courseQueryReq.getPage(), courseQueryReq.getSize());
        } else {
            PageHelper.startPage(1, 10);
        }
        List<Course> courseList = courseMapper.selectByExampleWithBLOBs(courseExample);
        return PageInfoUtil.getPageInfoResp(courseList, Course.class);
    }

    /**
     * 查询全部课程信息的业务方法
     *
     * @param courseQueryReq : 查询（分页）参数
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.Course>
     * @author h0ss
     */
    @Override
    public PageResp<Course> getAll(CourseQueryReq courseQueryReq) {
        CourseExample courseExample = new CourseExample();
        courseExample.setOrderByClause("sort");
        // 获取全部课程信息每次最多显示100条
        if (courseQueryReq.getPage() != null && courseQueryReq.getSize() != null) {
            PageHelper.startPage(courseQueryReq.getPage(), courseQueryReq.getSize());
        } else {
            PageHelper.startPage(1, 100);
        }
        List<Course> courseList = courseMapper.selectByExampleWithBLOBs(courseExample);
        return PageInfoUtil.getPageInfoResp(courseList, Course.class);
    }

    /**
     * 新增或编辑课程信息的业务方法
     *
     * @param courseSaveReq : 保存的课程信息数据
     * @author h0ss
     */
    @Override
    public void save(CourseSaveReq courseSaveReq) {
        // 创建一个新对象
        Course course = new Course();
        BeanUtils.copyProperties(courseSaveReq, course);
        // 判断是新增还是编辑
        if (course.getId() != null) {
            CourseExample courseExample = new CourseExample();
            CourseExample.Criteria criteria = courseExample.createCriteria();
            criteria.andIdEqualTo(course.getId());
            courseMapper.updateByExample(course, courseExample);
        } else {
            // 雪花算法生成id
            course.setId(snowFlake.nextId());
            // 创建日期
            course.setCreateTime(new Date());
            courseMapper.insertSelective(course);

        }
    }

    /**
     * 删除课程信息的业务方法
     *
     * @param id : 要删除的课程信息id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        CourseExample courseExample = new CourseExample();
        CourseExample.Criteria criteria = courseExample.createCriteria();
        criteria.andIdEqualTo(id);
        courseMapper.deleteByExample(courseExample);
    }

    /**
     * 对于指定字段的自增操作
     *
     * @param id  : 课程id
     * @param col : 字段简述
     * @author h0ss
     */
    @Override
    public void increment(Long id, String col) {
        CourseExample courseExample = new CourseExample();
        CourseExample.Criteria criteria = courseExample.createCriteria();
        criteria.andIdEqualTo(id);
        List<Course> courses = courseMapper.selectByExample(courseExample);
        if (!ObjectUtils.isEmpty(courses)) {
            Course course = courses.get(0);
            if (col1.equals(col)) {
                // 原子操作实现自增
                AtomicInteger atomicInteger = new AtomicInteger(course.getTotalMember());
                atomicInteger.getAndIncrement();
                course.setTotalMember(atomicInteger.get());
            } else if (col2.equals(col)) {
                // 原子操作实现自增
                AtomicInteger atomicInteger = new AtomicInteger(course.getTotalSection());
                atomicInteger.getAndIncrement();
                course.setTotalSection(atomicInteger.get());
            } else if (col3.equals(col)) {
                // 原子操作实现自增
                AtomicInteger atomicInteger = new AtomicInteger(course.getTotalPeriod());
                atomicInteger.getAndIncrement();
                course.setTotalPeriod(atomicInteger.get());
            } else {
                return;
            }
            courseMapper.updateByExampleSelective(course, courseExample);
        }
    }

    /**
     * 对于指定字段的自减操作
     *
     * @param id  : 课程id
     * @param col : 字段简述
     * @author h0ss
     */
    @Override
    public void decrement(Long id, String col) {
        CourseExample courseExample = new CourseExample();
        CourseExample.Criteria criteria = courseExample.createCriteria();
        criteria.andIdEqualTo(id);
        List<Course> courses = courseMapper.selectByExample(courseExample);
        if (!ObjectUtils.isEmpty(courses)) {
            Course course = courses.get(0);
            if (col1.equals(col)) {
                // 原子操作实现自减
                AtomicInteger atomicInteger = new AtomicInteger(course.getTotalMember());
                atomicInteger.getAndDecrement();
                course.setTotalMember(atomicInteger.get());
            } else if (col2.equals(col)) {
                // 原子操作实现自减
                AtomicInteger atomicInteger = new AtomicInteger(course.getTotalSection());
                atomicInteger.getAndDecrement();
                course.setTotalSection(atomicInteger.get());
            } else if (col3.equals(col)) {
                // 原子操作实现自减
                AtomicInteger atomicInteger = new AtomicInteger(course.getTotalPeriod());
                atomicInteger.getAndDecrement();
                course.setTotalPeriod(atomicInteger.get());
            } else {
                return;
            }
            courseMapper.updateByExampleSelective(course, courseExample);
        }
    }

    /**
     * 获取热门课程数据的业务方法[每日更新一次 写入缓存]
     *
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.entity.Course>
     * @author h0ss
     */
    @Override
    public List<CourseCustResp> getHot() {
        Set<String> hotList = stringRedisTemplate.opsForZSet().reverseRange(RedisKeyUtil.ZSET_KEY_HOT_COURSE, 0, 6);
        List<CourseCustResp> courseList = RedisOpsUtil.dumpZset(hotList, CourseCustResp.class);
        if (ObjectUtils.isEmpty(courseList)) {
            // 从数据库中取
            CourseExample courseExample = new CourseExample();
            CourseExample.Criteria criteria = courseExample.createCriteria();
            // 已上线的课程
            criteria.andStatusEqualTo(Boolean.TRUE);
            // 根据学员数来判定是否热门
            courseExample.setOrderByClause("total_member desc");
            // 只显示前六条数据
            PageHelper.startPage(1, 6);
            List<Course> hotCourses = courseMapper.selectByExampleWithBLOBs(courseExample);
            // 课程信息转换
            courseList = new ArrayList<>(hotCourses.size());
            for (Course course : hotCourses) {
                CourseCustResp custResp = new CourseCustResp();
                BeanUtils.copyProperties(course, custResp);
                courseList.add(custResp);
                // 存redis 根据学习人数排序
                stringRedisTemplate
                        .opsForZSet()
                        .add(RedisKeyUtil.ZSET_KEY_HOT_COURSE, JSON.toJSONString(custResp), custResp.getTotalMember());
                // 每日刷新一次 设置过期时间为1天
                stringRedisTemplate.expire(RedisKeyUtil.ZSET_KEY_HOT_COURSE, 3600 * 24, TimeUnit.SECONDS);
            }
        }
        return courseList;
    }

    /**
     * 根据传入信息获取课程详情的业务方法
     *
     * @param courseCommonReq : 查询参数封装实体类
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.resp.CourseCustResp>
     * @author h0ss
     */
    @Override
    public List<CourseCustResp> getContent(CourseCommonReq courseCommonReq) {
        final String memberSort = "totalMember";
        // 这里需要多一步判断 不可直接查询 因为mybatis中排序用到的是$ 如果不判断就查询可能会造成注入危险
        if (memberSort.equals(courseCommonReq.getSortField())) {
            return courseCustMapper.selectCourseDetail(courseCommonReq.getCourseId(), courseCommonReq.getGradeId(), "total_member");
        }
        return courseCustMapper.selectCourseDetail(courseCommonReq.getCourseId(), courseCommonReq.getGradeId(), "sort");
    }

    /**
     * 课程日榜获取
     *
     * @param day : 当日
     * @return : java.util.Map<java.lang.Long,cn.gpnusz.ucloudteachentity.resp.CourseCustResp>
     * @throws InterruptedException 被打断异常
     * @author h0ss
     */
    @Override
    public Map<Long, CourseCustResp> getTodayHot(Integer day) throws InterruptedException {
        if (day == null || day.equals(0)) {
            day = LocalDate.now().getDayOfMonth();
        }
        // 这里拿到的是排行榜上课程的id
        Set<String> keySet = stringRedisTemplate.opsForZSet().reverseRange(RedisKeyUtil.getHotDayKey(day), 0, 10);
        if (ObjectUtils.isEmpty(keySet)) {
            return null;
        }
        // 多线程取出课程的信息
        ConcurrentHashMap<Long, CourseCustResp> result = new ConcurrentHashMap<>(keySet.size());
        // 用于等待结果获取
        CountDownLatch cdl = new CountDownLatch(keySet.size());
        for (String key : keySet) {
            threadPoolExecutor.execute(() -> {
                // 根据课程id 取出课程信息 先查缓存 再查数据库
                String courseCacheKey = RedisKeyUtil.getCourseKey(key);
                String value = stringRedisTemplate.opsForValue().get(courseCacheKey);
                if (!ObjectUtils.isEmpty(value)) {
                    CourseCustResp ccr = JSON.parseObject(value, CourseCustResp.class);
                    // 从缓存中取出数据
                    if (!ObjectUtils.isEmpty(ccr)) {
                        result.put(cdl.getCount(), ccr);
                    }
                }
                // 从数据库中取出数据
                if (!result.containsKey(cdl.getCount())) {
                    CourseCustResp ccr = courseCustMapper.getCourseDetail(Long.valueOf(key));
                    result.put(cdl.getCount(), ccr);
                    // 写redis
                    stringRedisTemplate.opsForValue().set(RedisKeyUtil.getCourseKey(key), JSON.toJSONString(ccr), 3600 * 24, TimeUnit.SECONDS);
                }
                cdl.countDown();
            });
        }
        cdl.await();
        return result;
    }

    /**
     * 查询教师与课程是否匹配
     *
     * @param teacher  : 教师名称
     * @param courseId : 课程id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    @Override
    public Boolean checkTeacher(String teacher, Long courseId) {
        CourseExample example = new CourseExample();
        CourseExample.Criteria criteria = example.createCriteria();
        criteria.andTeacherEqualTo(teacher);
        long count = courseMapper.countByExample(example);
        return count == 0L ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * 获取课程数量
     *
     * @return : java.lang.Long
     * @author h0ss
     */
    @Override
    public Long getCourseCount() {
        return courseMapper.countByExample(null);
    }
}
