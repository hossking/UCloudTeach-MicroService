package cn.gpnusz.ucloudteachentity.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Date;

public class LiveContentSaveReq implements Serializable {
    private static final Long serialVersionUID = 37032209340656006L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long liveId;

    private String name;

    private Integer sort;

    private String backVideo;

    private Integer liveTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date beginDate;

    private Boolean status;

    public LiveContentSaveReq() {
    }

    public LiveContentSaveReq(Long id, Long liveId, String name, Integer sort, String backVideo, Integer liveTime, Date beginTime, Date beginDate, Boolean status) {
        this.id = id;
        this.liveId = liveId;
        this.name = name;
        this.sort = sort;
        this.backVideo = backVideo;
        this.liveTime = liveTime;
        this.beginTime = beginTime;
        this.beginDate = beginDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLiveId() {
        return liveId;
    }

    public void setLiveId(Long liveId) {
        this.liveId = liveId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getBackVideo() {
        return backVideo;
    }

    public void setBackVideo(String backVideo) {
        this.backVideo = backVideo;
    }

    public Integer getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(Integer liveTime) {
        this.liveTime = liveTime;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LiveContentSaveReq{" +
                "id=" + id +
                ", liveId=" + liveId +
                ", name='" + name + '\'' +
                ", sort=" + sort +
                ", backVideo='" + backVideo + '\'' +
                ", liveTime=" + liveTime +
                ", beginTime=" + beginTime +
                ", beginDate=" + beginDate +
                ", status=" + status +
                '}';
    }
}