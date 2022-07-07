package cn.gpnusz.courseinterface.service;

import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.Course;
import cn.gpnusz.ucloudteachentity.req.CourseCommonReq;
import cn.gpnusz.ucloudteachentity.req.CourseQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseSaveReq;
import cn.gpnusz.ucloudteachentity.resp.CourseCustResp;

import java.util.List;
import java.util.Map;

/**
 * @author h0ss
 * @description 课程信息业务接口
 * @date 2021/11/14 20:09
 */

public interface CourseService {
    /**
     * 按传入条件查询课程信息的业务方法
     *
     * @param courseQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.Course>
     * @author h0ss
     */
    PageResp<Course> getAllByCondition(CourseQueryReq courseQueryReq);

    /**
     * 查询全部课程信息的业务方法
     *
     * @param courseQueryReq : 查询（分页）参数
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.Course>
     * @author h0ss
     */
    PageResp<Course> getAll(CourseQueryReq courseQueryReq);

    /**
     * 新增或编辑课程信息的业务方法
     *
     * @param courseSaveReq : 保存的课程信息数据
     * @author h0ss
     */
    void save(CourseSaveReq courseSaveReq);

    /**
     * 删除课程信息的业务方法
     *
     * @param id : 要删除的课程信息id
     * @author h0ss
     */
    void delete(Long id);

    /**
     * 对于指定字段的自增操作
     *
     * @param id  : 课程id
     * @param col : 字段简述
     * @author h0ss
     */
    void increment(Long id, String col);

    /**
     * 对于指定字段的自减操作
     *
     * @param id  : 课程id
     * @param col : 字段简述
     * @author h0ss
     */
    void decrement(Long id, String col);

    /**
     * 获取热门课程数据的业务方法[每日更新一次 写入缓存]
     *
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.entity.Course>
     * @author h0ss
     */
    List<CourseCustResp> getHot();

    /**
     * 根据传入信息获取课程详情的业务方法
     *
     * @param courseCommonReq : 查询参数封装实体类
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.resp.CourseCustResp>
     * @author h0ss
     */
    List<CourseCustResp> getContent(CourseCommonReq courseCommonReq);


    /**
     * 课程日榜获取
     *
     * @param day : 当日
     * @return : java.util.Map<java.lang.Long,cn.gpnusz.ucloudteachentity.resp.CourseCustResp>
     * @throws InterruptedException 被打断异常
     * @author h0ss
     */
    Map<Long, CourseCustResp> getTodayHot(Integer day) throws InterruptedException;

    /**
     * 查询教师与课程是否匹配
     *
     * @param teacher  : 教师名称
     * @param courseId : 课程id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    Boolean checkTeacher(String teacher, Long courseId);

    /**
     * 获取课程数量
     *
     * @return : java.lang.Long
     * @author h0ss
     */
    Long getCourseCount();
}
