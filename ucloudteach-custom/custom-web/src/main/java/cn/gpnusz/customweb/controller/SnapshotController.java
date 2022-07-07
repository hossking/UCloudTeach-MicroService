package cn.gpnusz.customweb.controller;

import cn.gpnusz.custominterface.service.SnapshotService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.Snapshot;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author h0ss
 * @description 首页快照数据API
 * @date 2022/5/5 - 15:44
 */
@RestController
@RequestMapping("/api/admin/snapshot")
public class SnapshotController {

    @DubboReference(version = "1.0.0")
    private SnapshotService snapshotService;

    @GetMapping("/get")
    public CommonResp<Snapshot> get() {
        CommonResp<Snapshot> resp = new CommonResp<>();
        resp.setContent(snapshotService.getData());
        resp.setMessage("数据获取成功");
        return resp;
    }

    @GetMapping("/before/get")
    public CommonResp<List<Snapshot>> getBeforeData() {
        CommonResp<List<Snapshot>> resp = new CommonResp<>();
        resp.setContent(snapshotService.getBeforeData());
        resp.setMessage("数据获取成功");
        return resp;
    }
}