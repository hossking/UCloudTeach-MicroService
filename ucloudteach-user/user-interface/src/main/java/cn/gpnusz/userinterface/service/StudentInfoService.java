package cn.gpnusz.userinterface.service;


import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.StudentInfo;
import cn.gpnusz.ucloudteachentity.req.StuResetPasswordReq;
import cn.gpnusz.ucloudteachentity.req.StudentInfoQueryReq;
import cn.gpnusz.ucloudteachentity.req.StudentInfoSaveReq;

/**
 * @author h0ss
 * @description 学员信息系统业务接口
 * @date 2021/11/12 - 20:57
 */
public interface StudentInfoService {
    /**
     * 按传入条件查询学员信息的业务接口
     *
     * @param studentInfoQueryReq : 查询条件参数
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.StudentInfo>
     * @author h0ss
     */
    PageResp<StudentInfo> getAllByCondition(StudentInfoQueryReq studentInfoQueryReq);

    /**
     * 查询全部学员信息数据的业务接口
     *
     * @param studentInfoQueryReq : 查询（分页）参数
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.StudentInfo>
     * @author h0ss
     */
    PageResp<StudentInfo> getAll(StudentInfoQueryReq studentInfoQueryReq);

    /**
     * 新增或编辑学员信息的业务接口
     *
     * @param studentInfoSaveReq : 保存的学员信息数据
     * @author h0ss
     */
    void save(StudentInfoSaveReq studentInfoSaveReq);


    /**
     * 重置学员密码的操作接口
     *
     * @param stuResetPasswordReq : 重置密码对象
     * @author h0ss
     */
    void resetPassword(StuResetPasswordReq stuResetPasswordReq);

    /**
     * 封禁/解封学员的业务接口
     *
     * @param id   : 学员id
     * @param flag : 封禁为1 解封为0
     * @author h0ss
     */
    void banStudent(Long id, Boolean flag);

    /**
     * 删除学员信息的业务接口
     *
     * @param id : 要删除的学员信息id
     * @author h0ss
     */
    void delete(Long id);

    /**
     * 按传入的手机号查找对应的学员信息
     *
     * @param phone : 手机号
     * @return : cn.gpnusz.ucloudteachentity.entity.StudentInfo
     * @author h0ss
     */
    StudentInfo selectByPhone(String phone);

    /**
     * 获取学生数量
     *
     * @return : java.lang.Long
     * @author h0ss
     */
    Long getStuCount();
}
