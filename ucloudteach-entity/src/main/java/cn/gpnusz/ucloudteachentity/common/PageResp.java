package cn.gpnusz.ucloudteachentity.common;

import java.io.Serializable;
import java.util.List;

/**
 * @author h0ss
 * @description 封装分页信息响应数据
 * @date 2021/11/12 1:22
 */
public class PageResp<T> implements Serializable {
    private static final Long serialVersionUID = 305112889857456165L;

    /**
     * 响应数据总数
     */
    private Long total;

    /**
     * 响应数据集合
     */
    private List<T> list;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageResp{" +
                "total=" + total +
                ", list=" + list +
                '}';
    }
}
