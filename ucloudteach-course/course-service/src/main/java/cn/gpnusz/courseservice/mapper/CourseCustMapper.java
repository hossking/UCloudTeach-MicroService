package cn.gpnusz.courseservice.mapper;


import cn.gpnusz.ucloudteachentity.resp.CourseCustResp;

import java.util.List;

/**
 * @author h0ss
 * @description 自定义的课程查询mapper接口
 * @date 2021/11/24 - 14:29
 */
public interface CourseCustMapper {

    /**
     * 获取课程详情
     *
     * @param courseId  : 课程id
     * @param gradeId   : 年级id
     * @param sortField : 排序字段
     * @return : java.util.List<cn.gpnusz.ucloudteach.resp.CourseCustResp>
     * @author h0ss
     */
    List<CourseCustResp> selectCourseDetail(Long courseId, Long gradeId, String sortField);

    /**
     * 根据课程id获取课程信息【封装部分字段】
     *
     * @param courseId : 课程id
     * @return : cn.gpnusz.ucloudteach.resp.CourseCustResp
     * @author h0ss
     */
    CourseCustResp getCourseDetail(Long courseId);
}
