package cn.gpnusz.ucloudteachentity.resp;

import java.io.Serializable;

/**
 * @author h0ss
 * @description 管理员登录成功的返回实体类
 * @date 2021/11/23 3:10
 */
public class AdminLoginResp implements Serializable {
    private static final Long serialVersionUID = 865112889857456165L;

    private String token;

    private String username;

    private Boolean superFlag;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getSuperFlag() {
        return superFlag;
    }

    public void setSuperFlag(Boolean superFlag) {
        this.superFlag = superFlag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AdminLoginResp{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", superFlag=" + superFlag +
                '}';
    }
}