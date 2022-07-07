package cn.gpnusz.ucloudteachentity.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author h0ss
 * @description 封装年级信息保存对象
 * @date 2021/11/13 13:42
 */
public class GradeSaveReq implements Serializable {
    private static final Long serialVersionUID = 425112089107456165L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @NotBlank(message = "【年级名称】不能为空")
    private String name;

    @NotNull(message = "【排序权重】不能为空")
    private Integer order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOder(Integer order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "GradeSaveReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", order=" + order +
                '}';
    }
}