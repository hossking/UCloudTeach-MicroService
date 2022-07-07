package cn.gpnusz.uploadweb.controller;

import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.uploadinterface.service.UploadService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import javax.annotation.Resource;
import java.util.Map;


/**
 * @author h0ss
 * @description 上传接口
 * @date 2021/11/15 14:57
 */

@RestController
@RequestMapping("/api/admin/upload")
public class UploadController {

    @DubboReference(version = "1.0.0")
    //@Resource
    private UploadService uploadService;

    /**
     * 上传图片的接口
     *
     * @param file : 上传的图片文件
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping(value = "/upload-pic")
    public CommonResp<Object> fileUpload(@RequestParam(value = "file") MultipartFile file) {
        CommonResp<Object> resp = new CommonResp<>();
        resp.setContent(uploadService.uploadPic(file));
        return resp;
    }

    /**
     * 上传视频【不分片】
     *
     * @param file : 文件
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping(value = "/upload-video")
    public CommonResp<Object> uploadVideo(@RequestParam(value = "file") MultipartFile file) {
        CommonResp<Object> resp = new CommonResp<>();
        resp.setContent(uploadService.uploadVideo(file));
        return resp;
    }

    /**
     * 上传分片的接口
     *
     * @param file     : 文件信息
     * @param hash     : 文件哈希值
     * @param filename : 文件名
     * @param seq      : 分片序号
     * @param type     : 文件类型
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    @PostMapping("/upload-slice")
    public CommonResp<String> uploadSlice(@RequestParam(value = "file") MultipartFile file,
                                          @RequestParam(value = "hash") String hash,
                                          @RequestParam(value = "filename") String filename,
                                          @RequestParam(value = "seq") Integer seq,
                                          @RequestParam(value = "type") String type) {
        try {
            return uploadService.uploadSlice(file.getBytes(), hash, filename, seq, type);
        } catch (IOException e) {
            e.printStackTrace();
            CommonResp<String> resp = new CommonResp<>();
            resp.setSuccess(false);
            resp.setMessage("上传失败");
            return resp;
        }
    }

    /**
     * 合并分片
     *
     * @param map : 合并参数
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    @PostMapping("/merge")
    public CommonResp<String> merge(@RequestBody Map<String, String> map) {
        return uploadService.uploadMerge(map);
    }

    /**
     * 检查秒传
     *
     * @param hash : 文件hash
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    @GetMapping("/check")
    public CommonResp<String> checkUpload(@RequestParam("hash") String hash) {
        return uploadService.fastUpload(hash);
    }

}
