package cn.gpnusz.uploadservice.service;

import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.exception.BusinessException;
import cn.gpnusz.ucloudteachentity.exception.BusinessExceptionCode;
import cn.gpnusz.uploadinterface.service.UploadService;
import cn.gpnusz.uploadservice.util.UploadUtils;
import com.alibaba.fastjson.JSON;
import com.obs.services.model.PartEtag;
import com.obs.services.model.UploadPartRequest;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author h0ss
 * @description 上传文件业务方法
 * @date 2021/11/15 - 14:37
 */
@Component
@DubboService(version = "1.0.0", timeout = 10000, interfaceClass = UploadService.class)
public class UploadServiceImpl implements UploadService {

    @Resource
    private UploadUtils uploadUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 魔法值
     */
    private final static String[] TYPEVIDEO = {"0000002066747970", "52494646d07d6007", "464c560105000000"};
    private final String[] TYPEPIC = {"89504e470d0a1a0a0000", "ffd8ffe000104a464946"};

    /**
     * 用于上传图片的业务方法
     *
     * @param file : 需要上传的图片文件
     * @return : java.lang.String
     * @author h0ss
     */
    @Override
    public String uploadPic(Object file) {
        if (file instanceof MultipartFile) {
            try {
                MultipartFile realFile = (MultipartFile) file;
                byte[] fileBytes = realFile.getBytes();
                String fileName = realFile.getOriginalFilename();
                return doUpload(fileBytes, fileName, TYPEPIC);
            } catch (IOException e) {
                throw new BusinessException(BusinessExceptionCode.UPLOAD_FAIL);
            }
        }
        throw new BusinessException(BusinessExceptionCode.UPLOAD_FAIL);
    }

    /**
     * 用于上传视频的业务方法
     *
     * @param file : 需要上传的视频文件
     * @return : java.lang.String
     * @author h0ss
     */
    @Override
    public String uploadVideo(Object file) {
        if (file instanceof MultipartFile) {
            try {
                MultipartFile realFile = (MultipartFile) file;
                byte[] fileBytes = realFile.getBytes();
                String fileName = realFile.getOriginalFilename();
                return doUpload(fileBytes, fileName, TYPEVIDEO);
            } catch (IOException e) {
                throw new BusinessException(BusinessExceptionCode.UPLOAD_FAIL);
            }
        }
        throw new BusinessException(BusinessExceptionCode.UPLOAD_FAIL);
    }


    /**
     * 根据传入的文件和魔法值去执行上传方法
     *
     * @param file      : 文件
     * @param types     : 类型魔法数列表
     * @param fileName: 文件名
     * @return : java.lang.String
     * @author h0ss
     */
    @Override
    public String doUpload(byte[] file, String fileName, String[] types) {
        try {
            String res = uploadUtils.upload(file, fileName, types);
            if (res != null) {
                return res;
            } else {
                throw new BusinessException(BusinessExceptionCode.UPLOAD_FAIL);
            }
        } catch (IOException e) {
            throw new BusinessException(BusinessExceptionCode.UPLOAD_FAIL);
        }
    }

    /**
     * 分片上传
     *
     * @param file     : 文件流
     * @param hash     : 哈希值
     * @param filename : 文件名
     * @param seq      : 分片序号
     * @param type     : 文件类型
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    @Override
    public CommonResp<String> uploadSlice(byte[] file, String hash, String filename, Integer seq, String type) {
        CommonResp<String> resp = new CommonResp<>();
        String uploadId = stringRedisTemplate.opsForValue().get(RedisKeyUtil.getUploadIdKey(hash));
        if (uploadId == null || "".equals(uploadId)) {
            // 初始化分段 使用lua脚本保证原子性
            uploadUtils.initMultipart(hash, filename, type);
            uploadId = stringRedisTemplate.opsForValue().get(RedisKeyUtil.getUploadIdKey(hash));
        }
        // 开始上传分片任务
        PartEtag partEtag = uploadUtils.uploadPart(uploadId, file, filename, type, seq);
        stringRedisTemplate.opsForSet().add(RedisKeyUtil.getSliceListHashKey(hash), JSON.toJSONString(partEtag));
        resp.setMessage("分片上传成功");
        return resp;
    }

    /**
     * 分片合并
     *
     * @param fileInfo : 文件信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    @Override
    public CommonResp<String> uploadMerge(Map<String, String> fileInfo) {
        CommonResp<String> resp = new CommonResp<>();
        String filename = fileInfo.get("filename");
        String type = fileInfo.get("type");
        String hash = fileInfo.get("hash");
        if (filename != null && type != null && hash != null) {
            // 获取分片集合
            Set<String> etagList = stringRedisTemplate.opsForSet().members(RedisKeyUtil.getSliceListHashKey(hash));
            // 获取上传id
            String uploadId = stringRedisTemplate.opsForValue().get(RedisKeyUtil.getUploadIdKey(hash));
            if (etagList != null && etagList.size() > 0 && uploadId != null) {
                List<PartEtag> partEtags = new ArrayList<>(etagList.size());
                // 反序列化出来
                for (String etag : etagList) {
                    partEtags.add(JSON.parseObject(etag, PartEtag.class));
                }
                // 调用合并方法
                String fileSite = uploadUtils.mergePart(uploadId, partEtags, filename + '.' + type);
                // 地址存入redis 实现秒传
                stringRedisTemplate.opsForValue().set(RedisKeyUtil.getFinishHashKey(hash), fileSite);
                stringRedisTemplate.delete(RedisKeyUtil.getUploadIdKey(hash));
                stringRedisTemplate.delete(RedisKeyUtil.getSliceListHashKey(hash));
                resp.setContent(fileSite);
                resp.setMessage("上传成功");
                return resp;
            }
        }
        resp.setSuccess(false);
        resp.setMessage("上传失败");
        return resp;
    }

    /**
     * 极速秒传
     *
     * @param hash : 文件哈希值
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    @Override
    public CommonResp<String> fastUpload(String hash) {
        CommonResp<String> resp = new CommonResp<>();
        String key = RedisKeyUtil.getFinishHashKey(hash);
        String fileSite = stringRedisTemplate.opsForValue().get(key);
        // 文件已存在 直接返回地址
        if (fileSite != null) {
            resp.setSuccess(true);
            resp.setContent(fileSite);
            resp.setMessage("极速秒传成功");
        } else {
            resp.setSuccess(false);
            resp.setContent("");
            resp.setMessage("极速秒传失败");
        }
        return resp;
    }
}
