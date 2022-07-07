package cn.gpnusz.ucloudteachentity.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * @author h0ss
 * @description 邮件信息实体
 * @date 2022/4/4 - 12:31
 */
public class CertMailInfo implements Serializable {
    private static final Long serialVersionUID = 27153945510041856L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String subject;

    private String to;

    private String uname;

    private String course;

    private String wish;

    private String certUrl;

    public CertMailInfo() {
    }

    public CertMailInfo(Long id, String subject, String to, String uname, String course, String wish, String certUrl) {
        this.id = id;
        this.subject = subject;
        this.to = to;
        this.uname = uname;
        this.course = course;
        this.wish = wish;
        this.certUrl = certUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getWish() {
        return wish;
    }

    public void setWish(String wish) {
        this.wish = wish;
    }

    public String getCertUrl() {
        return certUrl;
    }

    public void setCertUrl(String certUrl) {
        this.certUrl = certUrl;
    }

    @Override
    public String toString() {
        return "CertMailInfo{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", to='" + to + '\'' +
                ", uname='" + uname + '\'' +
                ", course='" + course + '\'' +
                ", wish='" + wish + '\'' +
                ", certUrl='" + certUrl + '\'' +
                '}';
    }
}
