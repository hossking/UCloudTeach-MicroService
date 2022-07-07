package cn.gpnusz.liveweb.controller;

import cn.gpnusz.liveinterface.service.LiveContentService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.LiveContent;
import cn.gpnusz.ucloudteachentity.req.LiveContentQueryReq;
import cn.gpnusz.ucloudteachentity.req.LiveContentSaveReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author h0ss
 * @description 直播内容api
 * @date 2022/4/4 - 21:10
 */
@RestController
@RequestMapping("/api/admin/live/content")
public class LiveContentController {

    @DubboReference(version = "1.0.0")
    private LiveContentService liveContentService;

    @GetMapping("/list")
    public CommonResp<PageResp<LiveContent>> list(@Valid LiveContentQueryReq lcq) {
        CommonResp<PageResp<LiveContent>> resp = new CommonResp<>();
        resp.setContent(liveContentService.getAllByCondition(lcq));
        resp.setMessage("获取成功！");
        return resp;
    }

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody LiveContentSaveReq liveContentSaveReq) {
        liveContentService.save(liveContentSaveReq);
        return new CommonResp<>();
    }


    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        liveContentService.delete(id);
        return new CommonResp<>();
    }
}
