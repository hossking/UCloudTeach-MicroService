package cn.gpnusz.examinterface.service;


import cn.gpnusz.ucloudteachentity.common.PageReq;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.resp.ExamCust;

/**
 * @author h0ss
 * @description 操作考试信息信息的业务层
 * @date 2021/11/17 22:16
 */

public interface ExamInfoService {
    /**
     * 获取考试信息的业务方法
     *
     * @param pageReq : 查询信息
     * @return : cn.gpnusz.ucloudteach.common.PageResp<cn.gpnusz.ucloudteach.resp.ExamCust>
     * @author h0ss
     */
    PageResp<ExamCust> getAll(PageReq pageReq);
}
