package cn.gpnusz.ucloudteachadmin.service;

import cn.gpnusz.ucloudteachentity.common.PageReq;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.Admin;
import cn.gpnusz.ucloudteachentity.req.AdminSaveReq;


/**
 * @author h0ss
 * @description 超管服务接口
 * @date 2022/3/30 - 1:43
 */
public interface AdminService {

    /**
     * 查询管理员信息的业务方法
     *
     * @param pageReq : 查询（分页）参数
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.Admin>
     * @author h0ss
     */
    PageResp<Admin> getAll(PageReq pageReq);


    /**
     * 新增或编辑管理员信息的业务方法
     *
     * @param adminSaveReq : 保存的管理员信息数据
     * @author h0ss
     */
    void save(AdminSaveReq adminSaveReq);


    /**
     * 删除管理员的业务方法
     *
     * @param id : 要删除的管理员id
     * @author h0ss
     */
    void delete(Long id);

    /**
     * 根据id获取管理员信息
     *
     * @param id : 管理员id
     * @return : cn.gpnusz.ucloudteachentity.entity.Admin
     * @author h0ss
     */
    Admin getInfoById(Long id);
}
