package cn.gpnusz.courseservice.service;

import cn.gpnusz.courseinterface.service.CourseSectionService;
import cn.gpnusz.courseservice.entity.CourseSectionExample;
import cn.gpnusz.courseservice.mapper.CourseSectionMapper;
import cn.gpnusz.courseservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CourseSection;
import cn.gpnusz.ucloudteachentity.req.CourseSectionQueryReq;
import cn.gpnusz.ucloudteachentity.req.CourseSectionSaveReq;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author h0ss
 * @description 章节信息的业务层
 * @date 2021/11/14 23:06
 */

@Service
@DubboService(interfaceClass = CourseSectionService.class, version = "1.0.0", timeout = 10000)
public class CourseSectionServiceImpl implements CourseSectionService {
    @Resource
    private CourseSectionMapper courseSectionMapper;

    @Resource
    private CourseServiceImpl courseService;

    @Resource
    private SnowFlake snowFlake;

    private static final Logger LOG = LoggerFactory.getLogger(CourseSectionServiceImpl.class);

    /**
     * 用于查询章节的业务方法
     *
     * @param courseSectionQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CourseSection>
     * @author h0ss
     */
    @Override
    public PageResp<CourseSection> getAll(CourseSectionQueryReq courseSectionQueryReq) {
        CourseSectionExample courseSectionExample = new CourseSectionExample();
        CourseSectionExample.Criteria criteria = courseSectionExample.createCriteria();
        if (!ObjectUtils.isEmpty(courseSectionQueryReq.getCourseId())) {
            criteria.andCourseIdEqualTo(courseSectionQueryReq.getCourseId());
        }
        courseSectionExample.setOrderByClause("section");
        // 获取全部章节信息如果不指定每次最多显示100条
        if (courseSectionQueryReq.getPage() != null && courseSectionQueryReq.getSize() != null) {
            PageHelper.startPage(courseSectionQueryReq.getPage(), courseSectionQueryReq.getSize());
        } else {
            PageHelper.startPage(1, 100);
        }
        List<CourseSection> courseSectionList = courseSectionMapper.selectByExample(courseSectionExample);
        return PageInfoUtil.getPageInfoResp(courseSectionList, CourseSection.class);
    }

    /**
     * 新增或编辑章节信息的业务方法
     *
     * @param courseSectionSaveReq : 保存的章节信息数据
     * @author h0ss
     */
    @Override
    public void save(CourseSectionSaveReq courseSectionSaveReq) {
        // 创建一个新对象
        CourseSection courseSection = new CourseSection();
        BeanUtils.copyProperties(courseSectionSaveReq, courseSection);
        // 判断是新增还是编辑
        if (courseSection.getId() != null) {
            CourseSectionExample courseSectionExample = new CourseSectionExample();
            CourseSectionExample.Criteria criteria = courseSectionExample.createCriteria();
            criteria.andIdEqualTo(courseSection.getId());
            courseSectionMapper.updateByExampleSelective(courseSection, courseSectionExample);
        } else {
            // 雪花算法生成id
            courseSection.setId(snowFlake.nextId());
            courseSectionMapper.insert(courseSection);
            // 对章节数进行自增
            courseService.increment(courseSection.getCourseId(), "section");
        }
    }

    /**
     * 删除章节信息的业务方法
     *
     * @param id : 要删除的章节信息id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        CourseSectionExample courseSectionExample = new CourseSectionExample();
        CourseSectionExample.Criteria criteria = courseSectionExample.createCriteria();
        criteria.andIdEqualTo(id);
        List<CourseSection> courseSections = courseSectionMapper.selectByExample(courseSectionExample);
        courseSectionMapper.deleteByExample(courseSectionExample);
        if (!ObjectUtils.isEmpty(courseSections)) {
            courseService.decrement(courseSections.get(0).getCourseId(), "section");
        }
    }

}
