package cn.gpnusz.examservice.service;

import cn.gpnusz.examinterface.service.ExamInfoService;
import cn.gpnusz.examservice.mapper.ExamCustMapper;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachentity.common.PageReq;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.resp.ExamCust;
import com.github.pagehelper.PageHelper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author h0ss
 * @description 操作考试信息信息的业务层
 * @date 2021/11/17 22:16
 */

@DubboService(interfaceClass = ExamInfoService.class, version = "1.0.0", timeout = 10000)
public class ExamInfoServiceImpl implements ExamInfoService {
    @Resource
    private ExamCustMapper examCustMapper;


    /**
     * 获取考试信息的业务方法
     *
     * @param pageReq : 查询信息
     * @return : cn.gpnusz.ucloudteach.common.PageResp<cn.gpnusz.ucloudteach.resp.ExamCust>
     * @author h0ss
     */
    @Override
    public PageResp<ExamCust> getAll(PageReq pageReq) {
        // 获取全部考试信息,不设置则每次最多显示100条
        if (pageReq.getPage() != null && pageReq.getSize() != null) {
            PageHelper.startPage(pageReq.getPage(), pageReq.getSize());
        } else {
            PageHelper.startPage(1, 100);
        }
        List<ExamCust> examCustList = examCustMapper.selectExamInfos();
        return PageInfoUtil.getPageInfoResp(examCustList, ExamCust.class);
    }
}
