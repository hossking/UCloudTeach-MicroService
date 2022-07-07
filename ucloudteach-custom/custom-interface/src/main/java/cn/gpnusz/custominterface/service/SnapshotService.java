package cn.gpnusz.custominterface.service;


import cn.gpnusz.ucloudteachentity.entity.Snapshot;

import java.util.List;

/**
 * @author h0ss
 * @description 快照数据业务层
 * @date 2021/12/4 - 18:06
 */
public interface SnapshotService {

    /**
     * 生成平台数据快照
     *
     * @author h0ss
     */
    void generateSnapshot();

    /**
     * 获取快照数据的业务方法
     *
     * @return : cn.gpnusz.ucloudteach.entity.Snapshot
     * @author h0ss
     */
    Snapshot getData();

    /**
     * 获取前七天的数据
     *
     * @return : java.util.List<cn.gpnusz.ucloudteach.entity.Snapshot>
     * @author h0ss
     */
    List<Snapshot> getBeforeData();
}
