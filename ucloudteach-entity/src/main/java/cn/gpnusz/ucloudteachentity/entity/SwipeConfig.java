package cn.gpnusz.ucloudteachentity.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

public class SwipeConfig implements Serializable {
    private static final Long serialVersionUID = 205402889857456165L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String picUrl;

    private Integer sort;

    private String url;

    private Boolean type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", picUrl=").append(picUrl);
        sb.append(", sort=").append(sort);
        sb.append(", url=").append(url);
        sb.append(", type=").append(type);
        sb.append("]");
        return sb.toString();
    }
}