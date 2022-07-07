package cn.gpnusz.ucloudteachentity.req;

import java.io.Serializable;

/**
 * @author h0ss
 * @description 验证码校验的请求参数
 * @date 2021/12/5 - 14:47
 */
public class CheckCodeReq implements Serializable {
    private static final Long serialVersionUID = 101412889827456165L;
    private String ticket;

    private String randStr;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getRandStr() {
        return randStr;
    }

    public void setRandStr(String randStr) {
        this.randStr = randStr;
    }

    @Override
    public String toString() {
        return "CheckCodeReq{" +
                "ticket='" + ticket + '\'' +
                ", randStr='" + randStr + '\'' +
                '}';
    }
}
