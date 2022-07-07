package cn.gpnusz.examinterface.service;

import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.req.UserExamSaveReq;
import cn.gpnusz.ucloudteachentity.resp.UserExamDetailResp;
import cn.gpnusz.ucloudteachentity.resp.UserExamResp;

import java.util.List;

/**
 * @author h0ss
 * @description 用户操作考试信息业务层
 * @date 2021/11/28 3:27
 */
public interface UserExamService {
    /**
     * 用户查询已参加考试信息的业务方法
     *
     * @param userId: 用户id
     * @return : java.util.List<cn.gpnusz.ucloudteach.resp.UserExamResp>
     * @author h0ss
     */
    List<UserExamResp> selectUserExam(Long userId);

    /**
     * 用户查询考试结果的业务方法
     *
     * @param userId: 用户id
     * @param paperId : 试卷ID
     * @return : cn.gpnusz.ucloudteach.resp.UserExamResp
     * @author h0ss
     */
    UserExamResp getExamRes(Long userId, Long paperId);

    /**
     * 获取考试的试题信息【不带答案】
     *
     * @param userId : 用户id
     * @param id     : 试卷id
     * @return : java.util.List<cn.gpnusz.ucloudteach.resp.UserExamDetailResp>
     * @author h0ss
     */
    List<UserExamDetailResp> getExamDetail(Long userId, Long id);

    /**
     * 获取练习的试题信息【带答案】
     *
     * @param id : 试卷id
     * @return : java.util.List<cn.gpnusz.ucloudteach.resp.UserExamDetailResp>
     * @author h0ss
     */
    List<UserExamDetailResp> getExerciseDetail(Long id);

    /**
     * 检查试卷是否已经过期
     *
     * @param paperId : 试卷id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    Boolean checkValid(Long paperId);

    /**
     * 检查是否参加过考试
     *
     * @param userId: 用户id
     * @param paperId : 试卷id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    Boolean checkExamJoinStatus(Long userId, Long paperId);

    /**
     * 学员参与考试的业务方法
     *
     * @param userId: 用户id
     * @param paperId : 试卷id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> joinExam(Long userId, Long paperId);

    /**
     * 学员保存考试答案的业务方法
     * 异步提交批阅业务
     *
     * @param userId          : 用户id
     * @param userExamSaveReq : 作答信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> submitExam(Long userId, UserExamSaveReq userExamSaveReq);


    /**
     * 检查考试提交状态的业务方法
     *
     * @param userId  : 用户id
     * @param paperId : 试卷id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    Boolean checkSubmit(Long userId, Long paperId);

    /**
     * 获取考试剩余时间
     *
     * @param userId: 用户id
     * @param paperId : 试卷id
     * @return : java.lang.Integer
     * @author h0ss
     */
    Integer getSurplusTime(Long userId, Long paperId);

    /**
     * 暂存答卷信息
     *
     * @param userId          : 用户id
     * @param userExamSaveReq : 答卷信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> saveAnswer(Long userId, UserExamSaveReq userExamSaveReq);

    /**
     * 获取暂存答案
     *
     * @param userId  : 用户id
     * @param paperId : 试卷id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    CommonResp<String> loadAnswer(Long userId, Long paperId);
}
