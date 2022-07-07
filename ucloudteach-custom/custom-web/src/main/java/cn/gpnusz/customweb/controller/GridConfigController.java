package cn.gpnusz.customweb.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.gpnusz.custominterface.service.GridConfigService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.GridConfig;
import cn.gpnusz.ucloudteachentity.req.GridConfigSaveReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author h0ss
 * @description 操作菜单项配置的接口
 * @date 2021/11/21 - 15:00
 */

@RestController
@RequestMapping("/api/common/grid")
public class GridConfigController {
    @DubboReference(version = "1.0.0")
    private GridConfigService gridConfigService;

    /**
     * 获取菜单项配置信息的接口
     *
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.util.List<cn.gpnusz.ucloudteachentity.entity.GridConfig>>
     * @author h0ss
     */
    @GetMapping("/list")
    public CommonResp<List<GridConfig>> getAll() {
        CommonResp<List<GridConfig>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(gridConfigService.getAll());
        return resp;
    }

    /**
     * 保存菜单项配置信息的接口
     *
     * @param gridConfigSaveReq : 保存的对象
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @SaCheckRole("admin")
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody GridConfigSaveReq gridConfigSaveReq) {
        gridConfigService.save(gridConfigSaveReq);
        return new CommonResp<>();
    }

    /**
     * 根据id删除菜单配置项的接口
     *
     * @param id : 菜单配置项id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @SaCheckRole("admin")
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        gridConfigService.delete(id);
        return new CommonResp<>();
    }
}