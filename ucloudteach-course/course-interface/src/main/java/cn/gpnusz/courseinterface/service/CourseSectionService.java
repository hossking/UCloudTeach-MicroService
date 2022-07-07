package cn.gpnusz.courseinterface.service;

import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CourseSection;
import cn.gpnusz.ucloudteachentity.req.CourseSectionQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseSectionSaveReq;

/**
 * @author h0ss
 * @description 章节信息的业务接口
 * @date 2021/11/14 23:06
 */

public interface CourseSectionService {

    /**
     * 用于查询章节的业务方法
     *
     * @param courseSectionQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CourseSection>
     * @author h0ss
     */
    PageResp<CourseSection> getAll(CourseSectionQueryReq courseSectionQueryReq);

    /**
     * 新增或编辑章节信息的业务方法
     *
     * @param courseSectionSaveReq : 保存的章节信息数据
     * @author h0ss
     */
    void save(CourseSectionSaveReq courseSectionSaveReq);

    /**
     * 删除章节信息的业务方法
     *
     * @param id : 要删除的章节信息id
     * @author h0ss
     */
    void delete(Long id);

}
