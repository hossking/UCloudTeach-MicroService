package cn.gpnusz.uploadservice.util;

import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author h0ss
 * @description
 * @date 2021/11/15 - 13:37
 */

@Component
public class UploadUtils {
    @Value("${obs.endPoint}")
    private String endPoint;

    @Value("${obs.ak}")
    private String ak;

    @Value("${obs.sk}")
    private String sk;

    @Value("${obs.socketTimeout}")
    private Integer socketTimeout;

    @Value("${obs.connectionTimeout}")
    private Integer connectionTimeout;

    public static final String BUCKET_NAME = "ucloudteach";

    @Resource
    private ObsClient obsClient;

    /**
     * 为保证uploadId设置原子性 使用lua脚本
     */
    private static final String SET_UPLOAD_ID_LUA = "if redis.call('get',KEYS[1]) == false then redis.call('set', KEYS[1],ARGV[1]) else return 0 end";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 上传文件的工具方法
     *
     * @param fileBytes : 文件
     * @param types     : 限制的类型
     * @param fileName: 文件名
     * @return : java.lang.String
     * @author h0ss
     */
    public String upload(byte[] fileBytes, String fileName, String[] types) throws IOException {
        boolean checkFlag = false;
        // 通过文件头魔法值判断文件类型
        for (String type : types) {
            if (checkType(fileBytes, type, type.length())) {
                checkFlag = true;
                break;
            }
        }
        if (fileBytes != null || !checkFlag) {
            return null;
        }
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(socketTimeout);
        config.setConnectionTimeout(connectionTimeout);
        config.setEndPoint(endPoint);
        // 创建ObsClient实例
        try (ObsClient obsClient = new ObsClient(ak, sk, config)) {
            // 使用访问OBS
            if (fileName != null) {
                String fileMd5 = DigestUtils.md5DigestAsHex(fileBytes);
                String realFileName = fileMd5 + fileName.substring(fileName.lastIndexOf('.'));
                PutObjectResult res = obsClient.putObject(BUCKET_NAME, realFileName, new ByteArrayInputStream(fileBytes));
                return res.getObjectUrl();
            }
        }
        return null;
    }

    /**
     * 初始化obs分段任务
     *
     * @param hash     : 文件哈希值
     * @param filename : 文件名
     * @param type     : 文件类型
     * @author h0ss
     */
    public synchronized void initMultipart(String hash, String filename, String type) {
        String uploadId = stringRedisTemplate.opsForValue().get(RedisKeyUtil.getUploadIdKey(hash));
        if (uploadId == null || "".equals(uploadId)) {
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(BUCKET_NAME, filename + "." + type);
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.addUserMetadata("property", "property-value");
//            metadata.setContentType("text/plain");
//            request.setMetadata(metadata);
            InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);
            // 获取分段标志id
            uploadId = result.getUploadId();
            // 指定 lua 脚本，并且指定返回值类型
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(SET_UPLOAD_ID_LUA, Long.class);
            // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
            stringRedisTemplate.execute(redisScript, Collections.singletonList(RedisKeyUtil.getUploadIdKey(hash)), uploadId);
        }
    }

    /**
     * 上传分片
     *
     * @param uploadId  : 上传id
     * @param fileBytes : 文件流
     * @param filename  : 文件名
     * @param type      : 类型
     * @param seq       : 分片序号
     * @return : com.obs.services.model.PartEtag
     * @author h0ss
     */
    public PartEtag uploadPart(String uploadId, byte[] fileBytes, String filename, String type, Integer seq) {
        // 创建ObsClient实例
        // ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        // 上传分段
        UploadPartRequest request = new UploadPartRequest(BUCKET_NAME, filename + "." + type);
        // 设置Upload ID
        request.setUploadId(uploadId);
        // 设置分段号，范围是1~10000，
        request.setPartNumber(seq);
        // 设置将要上传的分片
        request.setInput(new ByteArrayInputStream(fileBytes));
        // 获取上传结果
        UploadPartResult result = obsClient.uploadPart(request);
        return new PartEtag(result.getEtag(), result.getPartNumber());
    }

    /**
     * 发出合并请求
     *
     * @param uploadId  : 上传id
     * @param partEtags : 分片列表
     * @param filename  : 文件名
     * @return : java.lang.String 文件地址
     * @author h0ss
     */
    public String mergePart(String uploadId, List<PartEtag> partEtags, String filename) {
        // 创建ObsClient实例
        // ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        // 合并请求
        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(BUCKET_NAME, filename, uploadId, partEtags);
        // 发起合并
        CompleteMultipartUploadResult result = obsClient.completeMultipartUpload(request);
        return result.getObjectUrl();
    }

    /**
     * 根据魔法值检查文件格式
     *
     * @param src  : 文件流
     * @param type : 类型魔法值
     * @param len  : 检验长度
     * @return : boolean
     * @author h0ss
     */
    public boolean checkType(byte[] src, String type, Integer len) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return false;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (stringBuilder.length() == len) {
                break;
            }
        }
        return type.equals(stringBuilder.toString());
    }
}
