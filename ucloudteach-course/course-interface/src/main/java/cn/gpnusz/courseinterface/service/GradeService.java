package cn.gpnusz.courseinterface.service;

import cn.gpnusz.ucloudteachentity.entity.Grade;
import cn.gpnusz.ucloudteachentity.req.GradeSaveReq;

import java.util.List;

/**
 * @author h0ss
 * @description 年级信息操作业务接口
 * @date 2021/11/13 13:33
 */
public interface GradeService {
    /**
     * 获取所有年级信息的业务方法
     *
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.entity.Grade>
     * @author h0ss
     */
    List<Grade> getAll();

    /**
     * 新增/编辑年级信息的业务方法 由于年级信息有限故不设置分页
     *
     * @param gradeSaveReq : 保存的年级信息
     * @author h0ss
     */
    void save(GradeSaveReq gradeSaveReq);

    /**
     * 删除年级信息的业务方法
     *
     * @param id : 要删除的年级id
     * @author h0ss
     */
    void delete(Long id);

}
