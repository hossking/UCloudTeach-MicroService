package cn.gpnusz.examinterface.service;


import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.req.ExamCheck;

/**
 * @author h0ss
 * @description 批阅试卷服务方法
 * @date 2022/3/23 - 19:47
 */
public interface ExamCheckService {

    /**
     * 检查是否需要自动批阅
     *
     * @param paperId : 试卷id
     * @return : boolean
     * @author h0ss
     */
    boolean needCheck(Long paperId);

    /**
     * 手动批阅题目
     *
     * @param userId       : 用户id
     * @param examCheckReq : 批阅信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> submitCheck(Long userId, ExamCheck examCheckReq);

    /**
     * 暂存批阅信息
     *
     * @param userId       : 用户id
     * @param examCheckReq : 批阅信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    CommonResp<Object> storeCheck(Long userId, ExamCheck examCheckReq);

    /**
     * 加载缓存批阅信息
     *
     * @param userId : 用户id
     * @param examId : 考试id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.req.ExamCheck>
     * @author h0ss
     */
    CommonResp<ExamCheck> loadCheck(Long userId, Long examId);
}
