package cn.gpnusz.courseservice.service;


import cn.gpnusz.courseinterface.service.CommonCommentService;
import cn.gpnusz.courseservice.mapper.CommentCustMapper;
import cn.gpnusz.ucloudteachentity.req.CourseCommentQueryReq;
import cn.gpnusz.ucloudteachentity.resp.CommonCommentResp;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author h0ss
 * @description 公共的获取评论信息的业务层
 * @date 2021/11/30 - 2:01
 */
@Service
@DubboService(interfaceClass = CommonCommentService.class, version = "1.0.0", timeout = 10000)
public class CommonCommentServiceImpl implements CommonCommentService {

    @Resource
    private CommentCustMapper commentCustMapper;

    /**
     * 获取评论信息的业务层
     *
     * @param courseCommentQueryReq :
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.resp.CommonCommentResp>
     * @author h0ss
     */
    @Override
    public List<CommonCommentResp> getComments(CourseCommentQueryReq courseCommentQueryReq) {
        // 查询条件判断
        if (ObjectUtils.isEmpty(courseCommentQueryReq.getCourseId())) {
            return null;
        }
        // 获取全部课程评论信息
        return commentCustMapper.getCommentByCourse(courseCommentQueryReq.getCourseId());
    }
}
