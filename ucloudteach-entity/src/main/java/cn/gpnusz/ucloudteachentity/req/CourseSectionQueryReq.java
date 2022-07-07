package cn.gpnusz.ucloudteachentity.req;

import cn.gpnusz.ucloudteachentity.common.PageReq;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author h0ss
 * @description 封装查询章节的信息
 * @date 2021/11/14 23:31
 */
public class CourseSectionQueryReq extends PageReq {
    private static final Long serialVersionUID = 505135663457456165L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "CourseSectionQueryReq{" +
                "courseId=" + courseId +
                "} " + super.toString();
    }
}