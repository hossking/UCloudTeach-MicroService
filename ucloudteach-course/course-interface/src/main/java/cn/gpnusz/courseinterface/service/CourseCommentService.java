package cn.gpnusz.courseinterface.service;

import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CourseComment;
import cn.gpnusz.ucloudteachentity.req.CourseCommentQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseCommentSaveReq;

/**
 * @author h0ss
 * @description 操作课程评论信息的业务层
 * @date 2021/11/17 22:16
 */

public interface CourseCommentService {
    /**
     * 按传入的条件查询课程评论
     *
     * @param courseCommentQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CourseComment>
     * @author h0ss
     */
    PageResp<CourseComment> getAllByCondition(CourseCommentQueryReq courseCommentQueryReq);

    /**
     * 新增或编辑课程评论信息的业务方法
     *
     * @param courseCommentSaveReq : 保存的课程评论信息数据
     * @author h0ss
     */
    void save(CourseCommentSaveReq courseCommentSaveReq);

    /**
     * 删除课程评论信息的业务方法
     *
     * @param id : 要删除的课程评论信息id
     * @author h0ss
     */
    void delete(Long id);


    /**
     * 查询评论和发布人是否匹配
     *
     * @param studentId : 用户id
     * @param id        : 评论id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    Boolean checkPair(Long studentId, Long id);
}
