package cn.gpnusz.ucloudteachentity.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author h0ss
 * @description 自定义课程信息返回类
 * @date 2021/11/22 - 18:03
 */

public class CourseCustResp implements Serializable {
    private static final Long serialVersionUID = 705111889167456165L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String teacher;

    private String name;

    private String cover;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long subjectId;

    private Integer totalMember;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer totalPeriod;

    private String description;

    private Integer totalSection;

    private BigDecimal price;

    private Boolean type;

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }


    public Integer getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(Integer totalMember) {
        this.totalMember = totalMember;
    }

    public Integer getTotalSection() {
        return totalSection;
    }

    public void setTotalSection(Integer totalSection) {
        this.totalSection = totalSection;
    }

    public void setCreateTime(Date createTime) {
        Date date = new Date();
        if (!ObjectUtils.isEmpty(createTime)) {
            BeanUtils.copyProperties(createTime, date);
        }
        this.createTime = date;
    }

    public Date getCreateTime() {
        Date date = new Date();
        if (!ObjectUtils.isEmpty(createTime)) {
            BeanUtils.copyProperties(createTime, date);
        }
        return date;
    }

    public Integer getTotalPeriod() {
        return totalPeriod;
    }

    public void setTotalPeriod(Integer totalPeriod) {
        this.totalPeriod = totalPeriod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "CourseCustResp{" +
                "id=" + id +
                ", teacher='" + teacher + '\'' +
                ", name='" + name + '\'' +
                ", cover='" + cover + '\'' +
                ", subjectId=" + subjectId +
                ", totalMember=" + totalMember +
                ", createTime=" + createTime +
                ", totalPeriod=" + totalPeriod +
                ", description='" + description + '\'' +
                ", totalSection=" + totalSection +
                ", price=" + price +
                ", type=" + type +
                '}';
    }
}
