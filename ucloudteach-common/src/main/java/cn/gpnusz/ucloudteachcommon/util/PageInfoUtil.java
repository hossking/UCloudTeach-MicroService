package cn.gpnusz.ucloudteachcommon.util;

import cn.gpnusz.ucloudteachentity.common.PageResp;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author h0ss
 * @description 分页对象工具类
 * @date 2022/4/3 - 13:40
 */
public class PageInfoUtil {

    /**
     * 生成分页对象结果
     *
     * @param entityList : 对象集合
     * @param clazz      : 对象Class
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<T>
     * @author h0ss
     */
    public static <T> PageResp<T> getPageInfoResp(List<T> entityList, Class<? extends T> clazz) {
        if (entityList == null) {
            return new PageResp<>();
        }
        List<T> respList = new ArrayList<>(entityList.size());
        // 将结果从entityList 深拷贝到 pageInfo中
        for (T entity : entityList) {
            T obj;
            try {
                obj = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
            BeanUtils.copyProperties(entity, obj);
            respList.add(obj);
        }
        // 创建分页信息对象
        PageInfo<T> pageInfo = new PageInfo<>(entityList);
        // 返回分页结果对象
        PageResp<T> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }
}
