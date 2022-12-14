package cn.gpnusz.ucloudteachentity.req;

import cn.gpnusz.ucloudteachentity.common.PageReq;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author h0ss
 * @description 封装查询课程信息
 * @date 2021/11/14 19:57
 */
public class CourseQueryReq extends PageReq {
    private static final Long serialVersionUID = 13751128992336165L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    private String teacher;

    private Boolean status;

    private Boolean type;

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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CourseQueryReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teacher='" + teacher + '\'' +
                ", status=" + status +
                ", type=" + type +
                "} " + super.toString();
    }
}