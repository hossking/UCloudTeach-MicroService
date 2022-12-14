package cn.gpnusz.ucloudteachentity.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @description 管理员登录请求实体类
 * @author h0ss
 * @date 2021/11/23 3:12
 */
public class AdminLoginReq extends CheckCodeReq{
    private static final Long serialVersionUID = 8351128257451165L;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "用户名不合法")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AdminLoginReq{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}