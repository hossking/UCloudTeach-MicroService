package cn.gpnusz.ucloudteachentity.exception;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author h0ss
 * @description 自定义业务异常
 * @date 2021/11/11 21:39
 */

public enum BusinessExceptionCode implements Serializable{
    // 在这里声明业务异常
    PHONE_NUMBER_EXIST("手机号已存在"),
    STUDENT_NOT_EXIST("学员不存在"),
    UPLOAD_FAIL("上传失败"),
    LOGIN_FAIL("登录失败，用户名或密码错误"),
    SEND_CODE_FAIL("短信下发失败"),
    BE_BAN_FAIL("该账号处于封禁状态，请联系管理员"),
    PAY_CREATE_FAIL("订单创建失败，请稍候重试");

    private static final Long serialVersionUID = 2251118893357456165L;

    private final String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
