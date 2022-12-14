package cn.gpnusz.uploadweb.controller;

import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.uploadinterface.service.UploadService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author h0ss
 * @description 用户上传头像的接口
 * @date 2021/12/4 16:12
 */

@RestController
@RequestMapping("/api/user/upload")
public class UserUploadController {

    @DubboReference(version = "1.0.0")
    private UploadService uploadService;

    /**
     * 上传图片的接口
     *
     * @param file : 上传的图片文件
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping(value = "/pic")
    public CommonResp<Object> fileUpload(@RequestParam(value = "file") MultipartFile file) {
        CommonResp<Object> resp = new CommonResp<>();
        resp.setContent(uploadService.uploadPic(file));
        return resp;
    }
}
