package cn.gpnusz.ucloudteachcommon.util;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author h0ss
 * @description 解析zset的工具类
 * @date 2022/3/15 - 18:44
 */
public class RedisOpsUtil {

    public static <T> List<T> dumpZset(Set<String> set, Class<T> clazz) {
        List<T> result = null;
        if (set != null && !set.isEmpty()) {
            // 将数据反序列化 存入结果集
            result = new ArrayList<>(set.size());
            for (String ele : set) {
                T obj = JSON.parseObject(ele, clazz);
                result.add(obj);
            }
        }
        return result;
    }
}
