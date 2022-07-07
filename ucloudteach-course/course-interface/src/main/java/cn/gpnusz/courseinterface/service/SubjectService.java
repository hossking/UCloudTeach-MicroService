package cn.gpnusz.courseinterface.service;

import cn.gpnusz.ucloudteachentity.entity.Subject;
import cn.gpnusz.ucloudteachentity.req.SubjectSaveReq;

import java.util.List;

/**
 * @author h0ss
 * @description 科目信息业务层
 * @date 2021/11/14 0:22
 */

public interface SubjectService {
    /**
     * 获取全部科目信息的业务方法
     *
     * @return : java.util.List<cn.gpnusz.ucloudteach.entity.Subject>
     * @author h0ss
     */
    List<Subject> getAll();

    /**
     * 根据年级id获取科目信息
     *
     * @param gradeId : 年级id
     * @return : java.util.List<cn.gpnusz.ucloudteach.entity.Subject>
     * @author h0ss
     */
    List<Subject> getByGrade(Long gradeId);

    /**
     * 新增或编辑科目信息的业务方法
     *
     * @param subjectSaveReq : 保存的科目信息
     * @author h0ss
     */
    void save(SubjectSaveReq subjectSaveReq);

    /**
     * 删除科目信息的业务方法
     *
     * @param id : 要删除的科目id
     * @author h0ss
     */
    void delete(Long id);
}
