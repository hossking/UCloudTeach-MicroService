package cn.gpnusz.customweb.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.gpnusz.custominterface.service.SwipeConfigService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.entity.SwipeConfig;
import cn.gpnusz.ucloudteachentity.req.SwipeConfigSaveReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author h0ss
 * @description 操作轮播图配置的接口
 * @date 2021/11/21 - 15:07
 */

@RestController
@RequestMapping("/api/common/swipe")
public class SwipeConfigController {

    @DubboReference(version = "1.0.0")
    private SwipeConfigService swipeConfigService;

    /**
     * 获取轮播图配置信息的接口
     *
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.util.List<cn.gpnusz.ucloudteachentity.entity.SwipeConfig>>
     * @author h0ss
     */
    @GetMapping("/list")
    public CommonResp<List<SwipeConfig>> getAll() {
        CommonResp<List<SwipeConfig>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(swipeConfigService.getAll());
        return resp;
    }

    /**
     * 保存轮播图配置信息的接口
     *
     * @param swipeConfigSaveReq : 保存的对象
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @SaCheckRole("admin")
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody SwipeConfigSaveReq swipeConfigSaveReq) {
        swipeConfigService.save(swipeConfigSaveReq);
        return new CommonResp<>();
    }

    /**
     * 根据id删除轮播图的接口
     *
     * @param id : 轮播图id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @SaCheckRole("admin")
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        swipeConfigService.delete(id);
        return new CommonResp<>();
    }
}