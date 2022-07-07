//package cn.gpnusz.uploadweb.controller;
//
//import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
//import cn.gpnusz.ucloudteachentity.common.CommonResp;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.Resource;
//import java.io.*;
//import java.nio.ByteBuffer;
//import java.nio.channels.Channel;
//import java.nio.channels.FileChannel;
//import java.nio.file.Path;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author h0ss
// * @description
// * @date 2022/3/28 - 19:54
// */
//@RestController
//public class TestController {
//
//    private static final Map<String, String> FILE_MAP = new ConcurrentHashMap<>();
//
//
//    @PostMapping("/upload")
//    public CommonResp<String> upload(@RequestParam(value = "file") MultipartFile file,
//                                     @RequestParam(value = "hash") String hash,
//                                     @RequestParam(value = "filename") String filename,
//                                     @RequestParam(value = "seq") Integer seq,
//                                     @RequestParam(value = "type") String type) {
//        CommonResp<String> resp = new CommonResp<>();
//        System.out.println(file.getSize() + " " + hash + " " + filename + " " + seq + " " + type);
//        RandomAccessFile raf = null;
//        try {
//            File dir = new File("I:\\test\\" + hash);
//            if (!dir.exists()) {
//                dir.mkdir();
//            }
//            raf = new RandomAccessFile("I:\\test\\" + hash + "\\" + filename + "." + type + seq, "rw");
//            raf.write(file.getBytes());
//            return resp;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return resp;
//        } finally {
//            try {
//                if (raf != null) {
//                    raf.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @PostMapping("/merge")
//    public CommonResp<String> merge(@RequestBody MergeInfo mergeInfo) {
//        if (mergeInfo!=null) {
//            String filename = mergeInfo.getFilename();
//            String type = mergeInfo.getType();
//            String hash = mergeInfo.getHash();
//            uploadService.merge(filename, type, hash);
//        }
//        CommonResp<String> resp = new CommonResp<>();
//
//        File dir = new File("I:\\test\\" + hash);
//        if (!dir.exists()) {
//            resp.setSuccess(false);
//            resp.setMessage("合并失败");
//            return resp;
//        }
//        FileChannel out = null;
//        try (FileChannel in = new RandomAccessFile("I:\\test\\" + filename + '.' + type, "rw").getChannel()) {
//            int index = 0;
//            while (true) {
//                String sliceName = "I:\\test\\" + hash + '\\' + filename + '.' + type + index;
//                if (!new File(sliceName).exists()) {
//                    break;
//                }
//                out = new RandomAccessFile(sliceName, "r").getChannel();
//                in.transferFrom(out, 0, out.size());
//                out.close();
//                out = null;
//            }
//            // 文件合并完毕
//            // 执行本地存储服务/第三方存储服务上传 返回文件地址 这里假设是fileSite
//            in.close();
//            String fileSite = "";
//            resp.setContent(fileSite);
//            resp.setMessage("上传成功");
//            // 地址存入redis 实现秒传
//            // stringRedisTemplate.opsForValue().set("upload:finish:hash:" + hash, fileSite);
//            return resp;
//        } catch (IOException e) {
//            // ...记录日志..
//            e.printStackTrace();
//        } finally {
//            if (out != null) {
//                try {
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        resp.setSuccess(false);
//        resp.setMessage("上传失败，请稍后重试");
//        return resp;
//    }
//
//    @GetMapping("/check")
//    public CommonResp<String> checkUpload(@RequestParam("hash") String hash) {
//        //return uploadService.fastUpload(hash);
//        return null;
//    }
//}
