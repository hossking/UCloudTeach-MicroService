package cn.gpnusz.liveinterface.service;


import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.LiveContent;
import cn.gpnusz.ucloudteachentity.req.LiveContentQueryReq;
import cn.gpnusz.ucloudteachentity.req.LiveContentSaveReq;
import cn.gpnusz.ucloudteachentity.resp.LiveTokenResp;

/**
 * @author h0ss
 * @description 直播课程内容业务接口
 * @date 2022/4/4 20:49
 */

public interface LiveContentService {
    /**
     * 根据传入条件查询直播内容
     *
     * @param lcq : 查询实体
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.LiveContent>
     * @author h0ss
     */
    PageResp<LiveContent> getAllByCondition(LiveContentQueryReq lcq);

    /**
     * 保存直播内容
     *
     * @param liveContentSaveReq : 保存实体
     * @author h0ss
     */
    void save(LiveContentSaveReq liveContentSaveReq);

    /**
     * 删除直播内容
     *
     * @param id : 要删除的直播内容id
     * @author h0ss
     */
    void delete(Long id);

    /**
     * 根据id获取课时信息
     *
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.entity.LiveContent
     * @author h0ss
     */
    LiveContent getContent(Long contentId);

    /**
     * 开始直播
     *
     * @param userId    : 用户id
     * @param courseId  : 课程id
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.LiveTokenResp>
     * @author h0ss
     */
    CommonResp<LiveTokenResp> startLive(Long userId, Long courseId, Long contentId);

    /**
     * 结束直播
     *
     * @param userId    : 用户id
     * @param courseId  : 课程id
     * @param contentId : 课时id
     * @author h0ss
     */
    void finishLive(Long userId, Long courseId, Long contentId);

    /**
     * 用户参与直播
     *
     * @param userId    : 用户id
     * @param courseId  : 课程id
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.LiveTokenResp>
     * @author h0ss
     */
    CommonResp<LiveTokenResp> joinLive(Long userId, Long courseId, Long contentId);

    /**
     * 用户查询课时状态
     *
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Boolean>
     * @author h0ss
     */
    CommonResp<Boolean> checkStatus(Long contentId);

    /**
     * 重置直播开始时间
     *
     * @param courseId : 课程id
     * @author h0ss
     */
    void resetTime(Long courseId);
}
