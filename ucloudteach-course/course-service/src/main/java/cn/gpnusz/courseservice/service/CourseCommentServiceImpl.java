package cn.gpnusz.courseservice.service;

import cn.gpnusz.courseinterface.service.CourseCommentService;
import cn.gpnusz.courseservice.entity.CourseCommentExample;
import cn.gpnusz.courseservice.mapper.CourseCommentMapper;
import cn.gpnusz.courseservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CourseComment;
import cn.gpnusz.ucloudteachentity.req.CourseCommentQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseCommentSaveReq;
import com.github.pagehelper.PageHelper;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author h0ss
 * @description 操作课程评论信息的业务层
 * @date 2021/11/17 22:16
 */

@Service
@DubboService(interfaceClass = CourseCommentService.class, version = "1.0.0", timeout = 10000)
public class CourseCommentServiceImpl implements CourseCommentService {
    @Resource
    private CourseCommentMapper courseCommentMapper;

    @Resource
    private SnowFlake snowFlake;

    private static final Logger LOG = LoggerFactory.getLogger(CourseCommentServiceImpl.class);

    /**
     * 按传入的条件查询课程评论
     *
     * @param courseCommentQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CourseComment>
     * @author h0ss
     */
    @Override
    public PageResp<CourseComment> getAllByCondition(CourseCommentQueryReq courseCommentQueryReq) {
        CourseCommentExample courseCommentExample = new CourseCommentExample();
        CourseCommentExample.Criteria criteria = courseCommentExample.createCriteria();
        // 查询条件判断
        if (!ObjectUtils.isEmpty(courseCommentQueryReq.getCourseId())) {
            criteria.andCourseIdEqualTo(courseCommentQueryReq.getCourseId());
        }
        if (!ObjectUtils.isEmpty(courseCommentQueryReq.getStudentId())) {
            criteria.andStudentIdEqualTo(courseCommentQueryReq.getStudentId());
        }
        if (!ObjectUtils.isEmpty(courseCommentQueryReq.getReplyId())) {
            criteria.andReplyIdEqualTo(courseCommentQueryReq.getReplyId());
        }
        // 获取全部课程评论信息,不设置则每次最多显示100条
        if (courseCommentQueryReq.getPage() != null && courseCommentQueryReq.getSize() != null) {
            PageHelper.startPage(courseCommentQueryReq.getPage(), courseCommentQueryReq.getSize());
        } else {
            PageHelper.startPage(1, 100);
        }
        courseCommentExample.setOrderByClause("top_flag desc, elite_flag desc, create_time desc");
        List<CourseComment> courseCommentList = courseCommentMapper.selectByExampleWithBLOBs(courseCommentExample);
        return PageInfoUtil.getPageInfoResp(courseCommentList, CourseComment.class);
    }

    /**
     * 新增或编辑课程评论信息的业务方法
     *
     * @param courseCommentSaveReq : 保存的课程评论信息数据
     * @author h0ss
     */
    @Override
    public void save(CourseCommentSaveReq courseCommentSaveReq) {
        // 创建一个新对象
        CourseComment courseComment = new CourseComment();
        BeanUtils.copyProperties(courseCommentSaveReq, courseComment);
        // 判断是新增还是编辑
        if (courseComment.getId() != null) {
            CourseCommentExample courseCommentExample = new CourseCommentExample();
            CourseCommentExample.Criteria criteria = courseCommentExample.createCriteria();
            criteria.andIdEqualTo(courseComment.getId());
            courseCommentMapper.updateByExampleSelective(courseComment, courseCommentExample);
        } else {
            // 如果精华、置顶标志为null则置为false
            if (ObjectUtils.isEmpty(courseComment.getEliteFlag())) {
                courseComment.setEliteFlag(Boolean.FALSE);
            }
            if (ObjectUtils.isEmpty(courseComment.getTopFlag())) {
                courseComment.setTopFlag(Boolean.FALSE);
            }
            // 设置创建日期时间
            courseComment.setCreateDate(new Date());
            courseComment.setCreateTime(new Date());
            // 雪花算法生成id
            courseComment.setId(snowFlake.nextId());
            courseCommentMapper.insert(courseComment);
        }
    }

    /**
     * 删除课程评论信息的业务方法
     *
     * @param id : 要删除的课程评论信息id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        CourseCommentExample courseCommentExample = new CourseCommentExample();
        CourseCommentExample.Criteria criteria = courseCommentExample.createCriteria();
        criteria.andIdEqualTo(id);
        courseCommentMapper.deleteByExample(courseCommentExample);
    }

    /**
     * 查询评论和发布人是否匹配
     *
     * @param studentId : 用户id
     * @param id        : 评论id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    @Override
    public Boolean checkPair(Long studentId, Long id) {
        CourseCommentExample example = new CourseCommentExample();
        CourseCommentExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        criteria.andStudentIdEqualTo(studentId);
        if (courseCommentMapper.countByExample(example) == 0) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }
}
