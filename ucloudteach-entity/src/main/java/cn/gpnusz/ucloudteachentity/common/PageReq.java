package cn.gpnusz.ucloudteachentity.common;

import javax.validation.constraints.Max;
import java.io.Serializable;

/**
 * @author h0ss
 * @description 封装页码请求参数
 * @date 2021/11/12 1:14
 */
public class PageReq implements Serializable {
    private static final Long serialVersionUID = 205112229857456165L;

    private Integer page;

    @Max(value = 100, message = "【每页容量】不能超过100")
    private Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PageReq{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}
