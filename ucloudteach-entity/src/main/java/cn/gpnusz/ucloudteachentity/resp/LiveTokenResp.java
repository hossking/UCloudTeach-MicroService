package cn.gpnusz.ucloudteachentity.resp;

import java.io.Serializable;

/**
 * @author h0ss
 * @description 生成的Live-Token信息实体
 * @date 2022/4/5 - 16:34
 */
public class LiveTokenResp implements Serializable {
    private static final long serialVersionUID = 65610338011856265L;

    private String token;

    private String channel;

    private Integer uid;

    private String role;

    public LiveTokenResp(String token, String channel, Integer uid, String role) {
        this.token = token;
        this.channel = channel;
        this.uid = uid;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "LiveTokenResp{" +
                "token='" + token + '\'' +
                ", channel='" + channel + '\'' +
                ", uid=" + uid +
                ", role='" + role + '\'' +
                '}';
    }
}
