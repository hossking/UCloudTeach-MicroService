package cn.gpnusz.courseservice.service;

import cn.gpnusz.courseinterface.service.CoursePeriodService;
import cn.gpnusz.courseservice.entity.CoursePeriodExample;
import cn.gpnusz.courseservice.mapper.CoursePeriodMapper;
import cn.gpnusz.courseservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.CoursePeriod;
import cn.gpnusz.ucloudteachentity.req.CoursePeriodQueryReq;
import cn.gpnusz.ucloudteachentity.req.CoursePeriodSaveReq;
import cn.gpnusz.ucloudteachentity.resp.CommonCoursePeriodResp;
import com.github.pagehelper.PageHelper;
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
 * @description 课时信息的业务层
 * @date 2021/11/15 0:05
 */

@Service
@DubboService(interfaceClass = CoursePeriodService.class, version = "1.0.0", timeout = 10000)
public class CoursePeriodServiceImpl implements CoursePeriodService {
    @Resource
    private CoursePeriodMapper coursePeriodMapper;

    @Resource
    private CourseServiceImpl courseService;

    @Resource
    private SnowFlake snowFlake;

    private static final Logger LOG = LoggerFactory.getLogger(CoursePeriodServiceImpl.class);

    /**
     * 用于查询课时的业务方法
     *
     * @param coursePeriodQueryReq : 查询对象
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.CoursePeriod>
     * @author h0ss
     */
    @Override
    public PageResp<CoursePeriod> getAll(CoursePeriodQueryReq coursePeriodQueryReq) {
        CoursePeriodExample coursePeriodExample = new CoursePeriodExample();
        CoursePeriodExample.Criteria criteria = coursePeriodExample.createCriteria();
        if (!ObjectUtils.isEmpty(coursePeriodQueryReq.getCourseId())) {
            criteria.andCourseIdEqualTo(coursePeriodQueryReq.getCourseId());
        }
        coursePeriodExample.setOrderByClause("period");
        if (coursePeriodQueryReq.getSectionId() != null) {
            criteria.andIdEqualTo(coursePeriodQueryReq.getSectionId());
        }
        // 获取全部课时信息如果不指定每次最多显示100条
        if (coursePeriodQueryReq.getPage() != null && coursePeriodQueryReq.getSize() != null) {
            PageHelper.startPage(coursePeriodQueryReq.getPage(), coursePeriodQueryReq.getSize());
        } else {
            PageHelper.startPage(1, 100);
        }
        List<CoursePeriod> coursePeriodList = coursePeriodMapper.selectByExampleWithBLOBs(coursePeriodExample);
        return PageInfoUtil.getPageInfoResp(coursePeriodList, CoursePeriod.class);
    }

    /**
     * 根据课程id获取课时公共信息的业务方法
     *
     * @param courseId : 课程id
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.resp.CommonCoursePeriodResp>
     * @author h0ss
     */
    @Override
    public List<CommonCoursePeriodResp> getAllCommon(Long courseId) {
        if (!ObjectUtils.isEmpty(courseId)) {
            // 根据课程id查询
            CoursePeriodExample coursePeriodExample = new CoursePeriodExample();
            CoursePeriodExample.Criteria criteria = coursePeriodExample.createCriteria();
            criteria.andCourseIdEqualTo(courseId);
            coursePeriodExample.setOrderByClause("period");
            // 获取全部课时公共信息
            List<CoursePeriod> coursePeriodList = coursePeriodMapper.selectByExampleWithBLOBs(coursePeriodExample);
            if (!coursePeriodList.isEmpty()) {
                ArrayList<CommonCoursePeriodResp> respList = new ArrayList<>(coursePeriodList.size());
                // 将公共信息取出
                for (CoursePeriod coursePeriod : coursePeriodList) {
                    CommonCoursePeriodResp resp = new CommonCoursePeriodResp();
                    BeanUtils.copyProperties(coursePeriod, resp);
                    respList.add(resp);
                }
                return respList;
            }
        }
        return null;
    }


    /**
     * 新增或编辑课时信息的业务方法
     *
     * @param coursePeriodSaveReq : 保存的课时信息数据
     * @author h0ss
     */
    @Override
    public void save(CoursePeriodSaveReq coursePeriodSaveReq) {
        // 创建一个新对象
        CoursePeriod coursePeriod = new CoursePeriod();
        BeanUtils.copyProperties(coursePeriodSaveReq, coursePeriod);
        // 判断是新增还是编辑
        if (coursePeriod.getId() != null) {
            CoursePeriodExample coursePeriodExample = new CoursePeriodExample();
            CoursePeriodExample.Criteria criteria = coursePeriodExample.createCriteria();
            criteria.andIdEqualTo(coursePeriod.getId());
            coursePeriodMapper.updateByExampleSelective(coursePeriod, coursePeriodExample);
        } else {
            // 雪花算法生成id
            coursePeriod.setId(snowFlake.nextId());
            coursePeriodMapper.insert(coursePeriod);
            // 对课时数进行自增
            courseService.increment(coursePeriod.getCourseId(), "period");
        }
    }

    /**
     * 删除课时信息的业务方法
     *
     * @param id : 要删除的课时信息id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        CoursePeriodExample coursePeriodExample = new CoursePeriodExample();
        CoursePeriodExample.Criteria criteria = coursePeriodExample.createCriteria();
        criteria.andIdEqualTo(id);
        List<CoursePeriod> coursePeriods = coursePeriodMapper.selectByExample(coursePeriodExample);
        coursePeriodMapper.deleteByExample(coursePeriodExample);
        if (!ObjectUtils.isEmpty(coursePeriods)) {
            // 对课时数进行自减
            courseService.decrement(coursePeriods.get(0).getCourseId(), "period");
        }
    }

}
