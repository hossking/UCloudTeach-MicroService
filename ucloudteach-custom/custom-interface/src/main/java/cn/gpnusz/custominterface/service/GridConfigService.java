package cn.gpnusz.custominterface.service;


import cn.gpnusz.ucloudteachentity.entity.GridConfig;
import cn.gpnusz.ucloudteachentity.req.GridConfigSaveReq;

import java.util.List;

/**
 * @author h0ss
 * @description 操作菜单项的业务层
 * @date 2021/11/21 14:57
 */
public interface GridConfigService {

    /**
     * 获取全部菜单项配置的业务方法
     *
     * @return : java.util.List<cn.gpnusz.ucloudteach.entity.GridConfig>
     * @author h0ss
     */
    List<GridConfig> getAll();

    /**
     * 保存菜单项配置的业务方法
     *
     * @param gridConfigSaveReq : 保存的对象
     * @author h0ss
     */
    void save(GridConfigSaveReq gridConfigSaveReq);

    /**
     * 根据id删除菜单项配置信息的业务方法
     *
     * @param id : 菜单项配置id
     * @author h0ss
     */
    void delete(Long id);
}