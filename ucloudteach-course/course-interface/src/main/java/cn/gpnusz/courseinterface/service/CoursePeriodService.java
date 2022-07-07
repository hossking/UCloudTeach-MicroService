package cn.gpnusz.courseinterface.service;

import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CoursePeriod;
import cn.gpnusz.ucloudteachentity.req.CoursePeriodQueryReq;
import cn.gpnusz.ucloudteachentity.req.CoursePeriodSaveReq;
import cn.gpnusz.ucloudteachentity.resp.CommonCoursePeriodResp;

import java.util.List;

/**
 * @author h0ss
 * @description 课时信息的业务层
 * @date 2021/11/15 0:05
 */

public interface CoursePeriodService {
    /**
     * 用于查询课时的业务方法
     *
     * @param coursePeriodQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CoursePeriod>
     * @author h0ss
     */
    PageResp<CoursePeriod> getAll(CoursePeriodQueryReq coursePeriodQueryReq);

    /**
     * 根据课程id获取课时公共信息的业务方法
     *
     * @param courseId : 课程id
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.resp.CommonCoursePeriodResp>
     * @author h0ss
     */
    List<CommonCoursePeriodResp> getAllCommon(Long courseId);


    /**
     * 新增或编辑课时信息的业务方法
     *
     * @param coursePeriodSaveReq : 保存的课时信息数据
     * @author h0ss
     */
    void save(CoursePeriodSaveReq coursePeriodSaveReq);

    /**
     * 删除课时信息的业务方法
     *
     * @param id : 要删除的课时信息id
     * @author h0ss
     */
    void delete(Long id);

}
