package cn.gpnusz.ucloudteachentity.req;


import cn.gpnusz.ucloudteachentity.common.PageReq;

/**
 * @author h0ss
 * @description 封装查询考试的信息
 * @date 2021/11/20 17:40
 */
public class ExamInfoQueryReq extends PageReq {
    private static final Long serialVersionUID = 635112809150456015L;

    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ExamInfoQueryReq{" +
                "status=" + status +
                '}';
    }
}