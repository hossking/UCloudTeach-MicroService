package cn.gpnusz.adminweb.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.gpnusz.ucloudteachadmin.service.AdminService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageReq;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.Admin;
import cn.gpnusz.ucloudteachentity.req.AdminSaveReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author h0ss
 * @description 操作管理员的接口
 * @date 2021/11/23 - 21:26
 */
@RestController
@SaCheckRole("superAdmin")
@RequestMapping("/api/admin/super")
public class AdminController {

    @DubboReference(version = "1.0.0")
    private AdminService adminService;


    @GetMapping("/list")
    public CommonResp<PageResp<Admin>> list(@Valid PageReq pageReq) {
        CommonResp<PageResp<Admin>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(adminService.getAll(pageReq));
        return resp;
    }


    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody AdminSaveReq adminSaveReq) {
        adminService.save(adminSaveReq);
        return new CommonResp<>();
    }


    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        adminService.delete(id);
        return new CommonResp<>();
    }
}
