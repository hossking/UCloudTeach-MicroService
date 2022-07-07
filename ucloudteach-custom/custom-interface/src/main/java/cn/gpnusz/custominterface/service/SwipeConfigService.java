package cn.gpnusz.custominterface.service;


import cn.gpnusz.ucloudteachentity.entity.SwipeConfig;
import cn.gpnusz.ucloudteachentity.req.SwipeConfigSaveReq;

import java.util.List;

/**
 * @author h0ss
 * @description 操作轮播图配置的业务层
 * @date 2021/11/21 14:55
 */
public interface SwipeConfigService {

    /**
     * 获取全部轮播图配置的业务方法
     *
     * @return : java.util.List<cn.gpnusz.ucloudteach.entity.SwipeConfig>
     * @author h0ss
     */
    List<SwipeConfig> getAll();

    /**
     * 保存轮播图配置的业务方法
     *
     * @param swipeConfigSaveReq : 保存的对象
     * @author h0ss
     */
    void save(SwipeConfigSaveReq swipeConfigSaveReq);

    /**
     * 根据id删除轮播图配置信息的业务方法
     *
     * @param id : 轮播图配置id
     * @author h0ss
     */
    void delete(Long id);
}