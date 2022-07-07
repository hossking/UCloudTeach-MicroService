package cn.gpnusz.courseinterface.service;

import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CourseMember;
import cn.gpnusz.ucloudteachentity.req.CourseMemberQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseMemberSaveReq;

/**
 * @author h0ss
 * @description 操作课程成员信息信息的业务层
 * @date 2021/11/17 2:11
 */

public interface CourseMemberService {
    /**
     * 按传入条件查询课程成员信息的业务方法
     *
     * @param courseMemberQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CourseMember>
     * @author h0ss
     */
    PageResp<CourseMember> getAllByCondition(CourseMemberQueryReq courseMemberQueryReq);

    /**
     * 查询全部课程成员信息的业务方法
     *
     * @param courseMemberQueryReq : 查询（分页）参数
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CourseMember>
     * @author h0ss
     */
    PageResp<CourseMember> getAll(CourseMemberQueryReq courseMemberQueryReq);

    /**
     * 新增或编辑课程成员信息的业务方法
     *
     * @param courseMemberSaveReq : 保存的课程成员信息数据
     * @param userFlag            : 是否用户操作
     * @author h0ss
     */
    void save(CourseMemberSaveReq courseMemberSaveReq, boolean userFlag);

    /**
     * 删除课程成员信息的业务方法
     *
     * @param id : 要删除的课程成员信息id
     * @author h0ss
     */
    void delete(Long id);

    /**
     * 查询学员是否参与了课程
     *
     * @param studentId: 学员id
     * @param courseId   : 课程id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    Boolean checkMember(Long studentId, Long courseId);

    /**
     * 查询课程学员完成课时数
     *
     * @param userId   : 学员id
     * @param courseId : 课程id
     * @return : java.lang.Integer
     * @author h0ss
     */
    Integer getCountRead(Long userId, Long courseId);
}
