package cn.gpnusz.uploadinterface.service;

import cn.gpnusz.ucloudteachentity.common.CommonResp;

import java.io.File;
import java.util.Map;

/**
 * @author h0ss
 * @description 上传文件服务接口
 * @date 2022/03/28 - 16:05
 */
public interface UploadService {

    /**
     * 用于上传图片的业务方法
     *
     * @param file : 需要上传的图片文件
     * @return : java.lang.String
     * @author h0ss
     */
    String uploadPic(Object file);

    /**
     * 用于上传视频的业务方法
     *
     * @param file : 需要上传的视频文件
     * @return : java.lang.String
     * @author h0ss
     */
    String uploadVideo(Object file);

    /**
     * 根据传入的文件和魔法值去执行上传方法
     *
     * @param file      : 文件
     * @param types     : 类型魔法数列表
     * @param fileName: 文件名
     * @return : java.lang.String
     * @author h0ss
     */
    String doUpload(byte[] file, String fileName, String[] types);

    /**
     * 分片上传接口
     *
     * @param file     : 文件流
     * @param hash     : 哈希值
     * @param filename : 文件名
     * @param seq      : 分片序号
     * @param type     : 文件类型
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    CommonResp<String> uploadSlice(byte[] file, String hash, String filename, Integer seq, String type);

    /**
     * 分片合并接口
     *
     * @param fileInfo : 文件信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    CommonResp<String> uploadMerge(Map<String, String> fileInfo);

    /**
     * 极速秒传接口
     *
     * @param hash : 文件哈希值
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.String>
     * @author h0ss
     */
    CommonResp<String> fastUpload(String hash);
}
