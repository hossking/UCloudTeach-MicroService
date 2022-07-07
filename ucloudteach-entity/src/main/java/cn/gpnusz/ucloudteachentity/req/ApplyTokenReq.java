package cn.gpnusz.ucloudteachentity.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * @author h0ss
 * @description 申请Live-Token的实体
 * @date 2022/4/5 - 16:28
 */
public class ApplyTokenReq implements Serializable {
    private static final long serialVersionUID = 75612338001866365L;

    private String appId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId;

    private Integer uid;

    private String channel;

    private Boolean publish;

    public ApplyTokenReq() {
    }

    public ApplyTokenReq(String appId, Long courseId, Integer uid, String channel, Boolean publish) {
        this.appId = appId;
        this.courseId = courseId;
        this.uid = uid;
        this.channel = channel;
        this.publish = publish;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    @Override
    public String toString() {
        return "ApplyTokenReq{" +
                "appId='" + appId + '\'' +
                ", courseId=" + courseId +
                ", uid=" + uid +
                ", channel='" + channel + '\'' +
                ", publish=" + publish +
                '}';
    }
}
