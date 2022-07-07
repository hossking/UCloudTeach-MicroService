package cn.gpnusz.ucloudteachentity.req;

import cn.gpnusz.ucloudteachentity.common.PageReq;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


/**
 * @author h0ss
 * @description 封装课程评论的查询信息
 * @date 2021/11/17 22:18
 */

public class CourseCommentQueryReq extends PageReq {
    private static final Long serialVersionUID = 455182889057406165L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long studentId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long replyId;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    @Override
    public String toString() {
        return "CourseCommentQueryReq{" +
                "courseId=" + courseId +
                ", studentId=" + studentId +
                ", replyId=" + replyId +
                '}';
    }
}