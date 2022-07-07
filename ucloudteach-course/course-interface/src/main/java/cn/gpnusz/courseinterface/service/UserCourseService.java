package cn.gpnusz.courseinterface.service;

import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.CertMailInfo;
import cn.gpnusz.ucloudteachentity.entity.CoursePeriod;
import cn.gpnusz.ucloudteachentity.entity.CourseRecord;
import cn.gpnusz.ucloudteachentity.req.CourseCommentSaveReq;
import cn.gpnusz.ucloudteachentity.req.CourseCommonReq;
import cn.gpnusz.ucloudteachentity.resp.CourseCustResp;
import cn.gpnusz.ucloudteachentity.resp.UserCourseResp;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author h0ss
 * @description 用户课程相关业务接口
 * @date 2021/11/28 1:07
 */
public interface UserCourseService {
    /**
     * 用户查询已学习课程的业务方法
     *
     * @param userId : 用户id
     * @return : java.util.List<cn.gpnusz.ucloudteach.resp.CourseMemberCustResp>
     * @author h0ss
     */
    List<UserCourseResp> selectUserCourse(Long userId);

    /**
     * 查询课时详情的业务方法[需要先判断用户是否已经参与课程学习]
     *
     * @param studentId : 用户id
     * @param periodId  : 课时id
     * @return : cn.gpnusz.ucloudteach.entity.CoursePeriod
     * @author h0ss
     */
    CommonResp<CoursePeriod> getContent(Long studentId, Long periodId);

    /**
     * 用户保存发表评论的业务方法
     *
     * @param userId               : 用户id
     * @param courseCommentSaveReq : 评论相关信息
     * @author h0ss
     */
    void saveComment(Long userId, CourseCommentSaveReq courseCommentSaveReq);

    /**
     * 删除评论的业务方法
     *
     * @param id     : 评论id
     * @param userId : 用户id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> deleteComment(Long userId, Long id);

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
    CommonResp<Object> applyCertificate(Long userId, String uname, String mail, Long courseId);

    /**
     * 用户申请加入课程
     *
     * @param userId : 用户id
     * @param req    : 包含id的入参对象
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    CommonResp<String> joinCourse(CourseCommonReq req, Long userId);

    /**
     * 创建课程订单的业务方法
     *
     * @param userId  : 用户id
     * @param content : 课程信息
     * @return : java.lang.String
     * @author h0ss
     */
    String createOrder(List<CourseCustResp> content, Long userId);

    /**
     * 生成订单对象
     *
     * @param courseId : 课程id
     * @param price    : 价格
     * @param recordId : 记录id
     * @param userId   : 用户id
     * @return : cn.gpnusz.ucloudteachentity.entity.CourseRecord
     * @author h0ss
     */
    CourseRecord generateRecord(Long courseId, BigDecimal price, long recordId, Long userId);

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
    CertMailInfo generateCertObj(String courseName, String uname, String cert, String sendWord, String to);

    /**
     * 将订单信息写入消息队列
     *
     * @param content    : 订单信息
     * @param routingKey : 路由key
     * @param msgType    : 消息类型
     * @author h0ss
     */
    void sendOrderToMq(CourseRecord content, String routingKey, String msgType);

    /**
     * 将证书信息发送到消息队列
     *
     * @param certMailInfo : 证书信息
     * @param routingKey   : 路由key
     * @param msgType      : 消息类型
     * @author h0ss
     */
    void sendCertToMq(CertMailInfo certMailInfo, String routingKey, String msgType);

    /**
     * 将学员信息写入【课程-学员】信息表
     *
     * @param studentId : 学员id
     * @param courseId  : 课程id
     * @param id        : 流水号
     * @author h0ss
     */
    void addMemberInfo(Long studentId, Long courseId, Long id);
}
