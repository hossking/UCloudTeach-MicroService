package cn.gpnusz.courseservice.service;

import cn.gpnusz.courseinterface.service.CourseMemberService;
import cn.gpnusz.courseservice.entity.CourseMemberExample;
import cn.gpnusz.courseservice.mapper.CourseMemberMapper;
import cn.gpnusz.courseservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CourseMember;
import cn.gpnusz.ucloudteachentity.req.CourseMemberQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseMemberSaveReq;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author h0ss
 * @description 操作课程成员信息信息的业务层
 * @date 2021/11/17 2:11
 */

@Service
@DubboService(interfaceClass = CourseMemberService.class, version = "1.0.0", timeout = 10000)
public class CourseMemberServiceImpl implements CourseMemberService {
    @Resource
    private CourseMemberMapper courseMemberMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private CourseServiceImpl courseService;

    private static final Logger LOG = LoggerFactory.getLogger(CourseMemberServiceImpl.class);

    /**
     * 按传入条件查询课程成员信息的业务方法
     *
     * @param courseMemberQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CourseMember>
     * @author h0ss
     */
    @Override
    public PageResp<CourseMember> getAllByCondition(CourseMemberQueryReq courseMemberQueryReq) {
        CourseMemberExample courseMemberExample = new CourseMemberExample();
        CourseMemberExample.Criteria criteria = courseMemberExample.createCriteria();
        if (!ObjectUtils.isEmpty(courseMemberQueryReq.getStudentId())) {
            criteria.andStudentIdEqualTo(courseMemberQueryReq.getStudentId());
        }
        if (!ObjectUtils.isEmpty(courseMemberQueryReq.getCourseId())) {
            criteria.andCourseIdEqualTo(courseMemberQueryReq.getCourseId());
        }
        if (!ObjectUtils.isEmpty(courseMemberQueryReq.getJoinDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                criteria.andJoinDateEqualTo(sdf.parse(courseMemberQueryReq.getJoinDate()));
            } catch (ParseException e) {
                LOG.error("日期转换有误，请输入正确日期");
            }
        }
        if (courseMemberQueryReq.getPage() != null && courseMemberQueryReq.getSize() != null) {
            PageHelper.startPage(courseMemberQueryReq.getPage(), courseMemberQueryReq.getSize());
        } else {
            PageHelper.startPage(1, 10);
        }
        List<CourseMember> courseMemberList = courseMemberMapper.selectByExample(courseMemberExample);
        return PageInfoUtil.getPageInfoResp(courseMemberList, CourseMember.class);
    }

    /**
     * 查询全部课程成员信息的业务方法
     *
     * @param courseMemberQueryReq : 查询（分页）参数
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.CourseMember>
     * @author h0ss
     */
    @Override
    public PageResp<CourseMember> getAll(CourseMemberQueryReq courseMemberQueryReq) {
        CourseMemberExample courseMemberExample = new CourseMemberExample();
        // 获取全部课程成员信息每次最多显示100条
        if (courseMemberQueryReq.getPage() != null && courseMemberQueryReq.getSize() != null) {
            PageHelper.startPage(courseMemberQueryReq.getPage(), courseMemberQueryReq.getSize());
        } else {
            PageHelper.startPage(1, 100);
        }
        List<CourseMember> courseMemberList = courseMemberMapper.selectByExample(courseMemberExample);
        return PageInfoUtil.getPageInfoResp(courseMemberList, CourseMember.class);
    }

    /**
     * 新增或编辑课程成员信息的业务方法
     *
     * @param courseMemberSaveReq : 保存的课程成员信息数据
     * @param userFlag            : 是否用户操作
     * @author h0ss
     */
    @Override
    public void save(CourseMemberSaveReq courseMemberSaveReq, boolean userFlag) {
        // 创建一个新对象
        CourseMember courseMember = new CourseMember();
        BeanUtils.copyProperties(courseMemberSaveReq, courseMember);
        // 判断是新增还是编辑
        if (courseMember.getId() != null && !userFlag) {
            // 更新的情况
            CourseMemberExample courseMemberExample = new CourseMemberExample();
            CourseMemberExample.Criteria criteria = courseMemberExample.createCriteria();
            criteria.andIdEqualTo(courseMember.getId());
            courseMemberMapper.updateByExampleSelective(courseMember, courseMemberExample);
        } else {
            courseMember.setFinishCourse(0);
            courseMember.setJoinDate(new Date());
            courseMember.setJoinTime(new Date());
            // 需要判断是否是用户购买课程导致的新增
            if (!userFlag) {
                // 雪花算法生成id
                courseMember.setId(snowFlake.nextId());
            }
            courseMemberMapper.insert(courseMember);
            // 同步课程成员数
            courseService.increment(courseMember.getCourseId(), "member");
        }
    }

    /**
     * 删除课程成员信息的业务方法
     *
     * @param id : 要删除的课程成员信息id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        CourseMemberExample courseMemberExample = new CourseMemberExample();
        CourseMemberExample.Criteria criteria = courseMemberExample.createCriteria();
        criteria.andIdEqualTo(id);
        List<CourseMember> courseMembers = courseMemberMapper.selectByExample(courseMemberExample);
        courseMemberMapper.deleteByExample(courseMemberExample);
        // 同步课程成员数
        if (!ObjectUtils.isEmpty(courseMembers)) {
            courseService.decrement(courseMembers.get(0).getCourseId(), "member");
        }
    }

    /**
     * 查询学员是否参与了课程
     *
     * @param studentId: 学员id
     * @param courseId   : 课程id
     * @return : java.lang.Boolean
     * @author h0ss
     */
    @Override
    public Boolean checkMember(Long studentId, Long courseId) {
        // 获取当前会话的id
        if (!ObjectUtils.isEmpty(studentId) && !ObjectUtils.isEmpty(courseId)) {
            // 查询学习记录
            CourseMemberExample example = new CourseMemberExample();
            CourseMemberExample.Criteria criteria = example.createCriteria();
            criteria.andStudentIdEqualTo(studentId);
            criteria.andCourseIdEqualTo(courseId);
            long record = courseMemberMapper.countByExample(example);
            // 存在即返回true
            if (record > 0) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 查询课程学员完成课时数
     *
     * @param userId   : 学员id
     * @param courseId : 课程id
     * @return : java.lang.Integer
     * @author h0ss
     */
    @Override
    public Integer getCountRead(Long userId, Long courseId) {
        CourseMemberExample courseMemberExample = new CourseMemberExample();
        CourseMemberExample.Criteria courseMemberExampleCriteria = courseMemberExample.createCriteria();
        courseMemberExampleCriteria.andStudentIdEqualTo(userId);
        courseMemberExampleCriteria.andCourseIdEqualTo(courseId);
        List<CourseMember> record = courseMemberMapper.selectByExample(courseMemberExample);
        // 查询不到记录返回-1
        if (record == null || record.isEmpty()) {
            return -1;
        }
        return record.get(0).getFinishCourse();
    }
}
