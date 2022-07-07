package cn.gpnusz.courseinterface.service;

import cn.gpnusz.ucloudteachentity.req.CourseCommentQueryReq;
import cn.gpnusz.ucloudteachentity.resp.CommonCommentResp;

import java.util.List;

/**
 * @author h0ss
 * @description 公共的获取评论信息的业务层
 * @date 2021/11/30 - 2:01
 */
public interface CommonCommentService {
    /**
     * 获取评论信息的业务层
     *
     * @param courseCommentQueryReq : 请求实体
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.resp.CommonCommentResp>
     * @author h0ss
     */
    List<CommonCommentResp> getComments(CourseCommentQueryReq courseCommentQueryReq);
}
