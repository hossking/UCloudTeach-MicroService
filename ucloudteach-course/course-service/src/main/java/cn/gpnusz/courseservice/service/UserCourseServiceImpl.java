package cn.gpnusz.courseservice.service;

import cn.gpnusz.courseinterface.service.UserCourseService;
import cn.gpnusz.courseservice.config.MqConfig;
import cn.gpnusz.courseservice.entity.CourseExample;
import cn.gpnusz.courseservice.entity.CoursePeriodExample;
import cn.gpnusz.courseservice.mapper.CourseMapper;
import cn.gpnusz.courseservice.mapper.CoursePeriodMapper;
import cn.gpnusz.courseservice.mapper.UserCourseCustMapper;
import cn.gpnusz.courseservice.util.SnowFlake;
import cn.gpnusz.payinterface.service.PayCreateService;
import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.CertMailInfo;
import cn.gpnusz.ucloudteachentity.entity.Course;
import cn.gpnusz.ucloudteachentity.entity.CoursePeriod;
import cn.gpnusz.ucloudteachentity.entity.CourseRecord;
import cn.gpnusz.ucloudteachentity.req.CourseCommentSaveReq;
import cn.gpnusz.ucloudteachentity.req.CourseCommonReq;
import cn.gpnusz.ucloudteachentity.req.CourseMemberSaveReq;
import cn.gpnusz.ucloudteachentity.resp.CourseCustResp;
import cn.gpnusz.ucloudteachentity.resp.UserCourseResp;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author h0ss
 * @description 用户课程相关业务层
 * @date 2021/11/28 1:07
 */
@Service
@DubboService(interfaceClass = UserCourseService.class, version = "1.0.0", timeout = 10000)
public class UserCourseServiceImpl implements UserCourseService {
    @Resource
    private UserCourseCustMapper userCourseCustMapper;

    @Resource
    private CoursePeriodMapper coursePeriodMapper;

    @Resource
    private CourseMemberServiceImpl courseMemberService;

    @Resource
    private CourseCommentServiceImpl courseCommentService;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CourseServiceImpl courseService;

    @DubboReference(version = "1.0.0", check = false)
    private PayCreateService payService;

    @Resource
    private AsyncServiceImpl asyncService;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户查询已学习课程的业务方法
     *
     * @param userId : 用户id
     * @return : java.util.List<cn.gpnusz.ucloudteach.resp.CourseMemberCustResp>
     * @author h0ss
     */
    @Override
    public List<UserCourseResp> selectUserCourse(Long userId) {
        // 查询当前用户学习的课程记录
        return userCourseCustMapper.selectCourseAndMember(userId);
    }

    /**
     * 查询课时详情的业务方法[需要先判断用户是否已经参与课程学习]
     *
     * @param studentId : 用户id
     * @param periodId  : 课时id
     * @return : cn.gpnusz.ucloudteach.entity.CoursePeriod
     * @author h0ss
     */
    @Override
    public CommonResp<CoursePeriod> getContent(Long studentId, Long periodId) {
        // 创建返回对象
        CommonResp<CoursePeriod> resp = new CommonResp<>();
        // 判断是否传入课时id
        if (!ObjectUtils.isEmpty(periodId)) {
            CoursePeriodExample coursePeriodExample = new CoursePeriodExample();
            CoursePeriodExample.Criteria criteria = coursePeriodExample.createCriteria();
            criteria.andIdEqualTo(periodId);
            List<CoursePeriod> periodContent = coursePeriodMapper.selectByExampleWithBLOBs(coursePeriodExample);
            if (!ObjectUtils.isEmpty(periodContent)) {
                Long courseId = periodContent.get(0).getCourseId();
                // 学员已参加课程情况
                if (courseMemberService.checkMember(studentId, courseId)) {
                    // 成员信息写表 消息队列异步
                    asyncService.writeRecord(studentId, courseId, periodId);
                    CoursePeriod content = periodContent.get(0);
                    resp.setContent(content);
                    resp.setMessage("数据获取成功");
                } else {
                    // 学员未参加课程情况
                    resp.setSuccess(false);
                    resp.setMessage("您暂未参与本课程学习，无法查看课程内容");
                }
                return resp;
            }
        }
        resp.setSuccess(false);
        return resp;
    }

    /**
     * 用户保存发表评论的业务方法
     *
     * @param userId               : 用户id
     * @param courseCommentSaveReq : 评论相关信息
     * @author h0ss
     */
    @Override
    public void saveComment(Long userId, CourseCommentSaveReq courseCommentSaveReq) {
        // 判断用户是否已加入课程
        if (courseMemberService.checkMember(userId, courseCommentSaveReq.getCourseId())) {
            courseCommentSaveReq.setStudentId(userId);
            courseCommentService.save(courseCommentSaveReq);
        }
    }

    /**
     * 删除评论的业务方法
     *
     * @param id     : 评论id
     * @param userId : 用户id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @Override
    public CommonResp<Object> deleteComment(Long userId, Long id) {
        CommonResp<Object> resp = new CommonResp<>();
        if (courseCommentService.checkPair(userId, id)) {
            courseCommentService.delete(id);
            resp.setMessage("删除成功");
        } else {
            resp.setSuccess(false);
            resp.setMessage("删除失败");
        }
        return resp;
    }

    /**
     * 用户获取证书的业务方法 [使用消息队列异步]
     *
     * @param userId   : 用户id
     * @param uname    : 用户名称
     * @param mail     : 用户邮箱
     * @param courseId : 课程id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @Override
    public CommonResp<Object> applyCertificate(Long userId, String uname, String mail, Long courseId) {
        CommonResp<Object> resp = new CommonResp<>();
        resp.setSuccess(false);
        // 查询课程信息
        CourseExample example = new CourseExample();
        CourseExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(courseId);
        List<Course> courses = courseMapper.selectByExampleWithBLOBs(example);
        if (courses == null || courses.isEmpty()) {
            resp.setMessage("证书获取失败");
            return resp;
        }
        Course course = courses.get(0);
        // 获取课程的总课时
        Integer totalPeriod = course.getTotalPeriod();
        // 获取学员的学习课时数
        Integer countRead = courseMemberService.getCountRead(userId, courseId);
        if (totalPeriod.equals(countRead)) {
            resp.setSuccess(true);
            // 构建消息对象
            CertMailInfo certMailInfo = generateCertObj(course.getName(), uname, course.getCertificate(), course.getSendWord(), mail);
            // 发送到消息队列
            sendCertToMq(certMailInfo, "cert.mail", "certMail");
            resp.setMessage("证书获取成功");
            return resp;
        }
        resp.setMessage("证书获取失败,请在学习完课程后领取");
        return resp;
    }

    /**
     * 用户申请加入课程
     *
     * @param userId : 用户id
     * @param req    : 包含id的入参对象
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    @Override
    public CommonResp<String> joinCourse(CourseCommonReq req, Long userId) {
        // 创建返回对象
        CommonResp<String> resp = new CommonResp<>();
        // 根据课程id获取课程相关信息
        List<CourseCustResp> content = courseService.getContent(req);
        // 获取课程信息
        if (ObjectUtils.isEmpty(content) || content.isEmpty()) {
            resp.setMessage("课程信息获取失败，请稍后重试");
            resp.setSuccess(false);
        } else {
            // 判断是否已经加入
            if (courseMemberService.checkMember(userId, content.get(0).getId())) {
                // 返回消息
                resp.setContent(String.valueOf(content.get(0).getId()));
                resp.setMessage("您已成功参与课程");
                return resp;
            }
            // 判断是否免费课程
            if (new BigDecimal("0.00").compareTo(content.get(0).getPrice()) == 0) {
                long recordId = snowFlake.nextId();
                // 添加用户到【课程成员信息表】
                addMemberInfo(userId, content.get(0).getId(), recordId);
                // 订单任务发送到队列
                sendOrderToMq(generateRecord(content.get(0).getId(), content.get(0).getPrice(), recordId, userId), "order.finish", "orderFinish");
                // 返回消息
                resp.setContent(String.valueOf(content.get(0).getId()));
                resp.setMessage("成功参与课程");
                return resp;
            }
            // 执行申请交易流程 写订单库
            resp.setContent(createOrder(content, userId));
        }
        return resp;
    }

    /**
     * 创建课程订单的业务方法
     *
     * @param userId  : 用户id
     * @param content : 课程信息
     * @return : java.lang.String
     * @author h0ss
     */
    @Override
    public String createOrder(List<CourseCustResp> content, Long userId) {
        // 生成订单ID
        long recordId = snowFlake.nextId();
        // 调用请求支付接口
        String res = payService.aliPay(content.get(0), recordId, userId);
        // 写入订单库[mq解耦]
        sendOrderToMq(generateRecord(content.get(0).getId(), content.get(0).getPrice(), recordId, userId), "order.create", "orderCreate");
        return res;
    }

    /**
     * 生成订单对象
     *
     * @param courseId : 课程id
     * @param price    : 价格
     * @param recordId : 记录id
     * @param userId   : 用户id
     * @return : cn.gpnusz.ucloudteach.entity.CourseRecord
     * @author h0ss
     */
    @Override
    public CourseRecord generateRecord(Long courseId, BigDecimal price, long recordId, Long userId) {
        CourseRecord courseRecord = new CourseRecord();
        // 设置id
        courseRecord.setId(recordId);
        // 设置课程id
        courseRecord.setCourseId(courseId);
        // 设置购买用户id
        courseRecord.setUserId(userId);
        // 设置价格
        courseRecord.setOrderAmount(price);
        // 设置状态为未付款
        courseRecord.setOrderStatus(0);
        // 设置时间相关信息
        Date now = new Date();
        courseRecord.setCreateTime(now);
        courseRecord.setCreateDate(now);
        courseRecord.setUpdateTime(now);
        courseRecord.setUpdateDate(now);
        return courseRecord;
    }

    /**
     * 生成证书信息对象
     *
     * @param courseName : 课程名
     * @param uname      : 学员名
     * @param cert       : 证书url
     * @param sendWord   : 寄语
     * @param to         : 发送邮箱
     * @return : cn.gpnusz.ucloudteachentity.entity.CertMailInfo
     * @author h0ss
     */
    @Override
    public CertMailInfo generateCertObj(String courseName, String uname, String cert, String sendWord, String to) {
        CertMailInfo info = new CertMailInfo();
        info.setId(snowFlake.nextId());
        info.setCourse(courseName);
        info.setCertUrl(cert);
        info.setWish(sendWord);
        info.setSubject("优云教平台证书领取通知");
        info.setTo(to);
        info.setUname(uname);
        return info;
    }

    /**
     * 将订单信息写入消息队列
     *
     * @param content    : 订单信息
     * @param routingKey : 路由key
     * @param msgType    : 消息类型
     * @author h0ss
     */
    @Override
    public void sendOrderToMq(CourseRecord content, String routingKey, String msgType) {
        // 设置id 方便回溯
        CorrelationData cd = new CorrelationData();
        // 根据不同记录类型去发送消息
        cd.setId(content.getId().toString());
        // 创建回调消息
        ReturnedMessage rm = new ReturnedMessage(
                new Message("订单消息回调".getBytes(StandardCharsets.UTF_8)),
                1, msgType, MqConfig.ORDER_EXCHANGE, routingKey);
        cd.setReturned(rm);
        // 发送到队列
        rabbitTemplate.convertAndSend(MqConfig.ORDER_EXCHANGE, routingKey, content, cd);
    }

    /**
     * 将证书信息发送到消息队列
     *
     * @param certMailInfo : 证书信息
     * @param routingKey   : 路由key
     * @param msgType      : 消息类型
     * @author h0ss
     */
    @Override
    public void sendCertToMq(CertMailInfo certMailInfo, String routingKey, String msgType) {
        // 设置id 方便回溯
        CorrelationData cd = new CorrelationData();
        // 根据不同记录类型去发送消息
        cd.setId(certMailInfo.getId().toString());
        // 创建回调消息
        ReturnedMessage rm = new ReturnedMessage(
                new Message("证书发送消息回调".getBytes(StandardCharsets.UTF_8)),
                1, msgType, MqConfig.CERT_EXCHANGE, routingKey);
        cd.setReturned(rm);
        // 发送到队列
        rabbitTemplate.convertAndSend(MqConfig.CERT_EXCHANGE, routingKey, certMailInfo, cd);
    }

    /**
     * 将学员信息写入【课程-学员】信息表
     *
     * @param studentId : 学员id
     * @param courseId  : 课程id
     * @param id        : 流水号
     * @author h0ss
     */
    @Override
    public void addMemberInfo(Long studentId, Long courseId, Long id) {
        if (!ObjectUtils.isEmpty(studentId) && !ObjectUtils.isEmpty(courseId) && !ObjectUtils.isEmpty(id)) {
            CourseMemberSaveReq req = new CourseMemberSaveReq();
            req.setId(id);
            req.setStudentId(studentId);
            req.setCourseId(courseId);
            courseMemberService.save(req, true);
            String hotKey = RedisKeyUtil.getHotDayKey(LocalDate.now().getDayOfMonth());
            // 排行榜自增
            stringRedisTemplate.opsForZSet().incrementScore(hotKey, String.valueOf(courseId), 1);
        }
    }
}
