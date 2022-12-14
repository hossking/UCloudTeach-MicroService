package cn.gpnusz.courseservice.service;

import cn.gpnusz.courseinterface.service.GradeService;
import cn.gpnusz.courseservice.entity.GradeExample;
import cn.gpnusz.courseservice.mapper.GradeMapper;
import cn.gpnusz.courseservice.util.SnowFlake;
import cn.gpnusz.ucloudteachentity.entity.Grade;
import cn.gpnusz.ucloudteachentity.req.GradeSaveReq;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author h0ss
 * @description 年级信息操作业务层
 * @date 2021/11/13 13:33
 */
@Service
@DubboService(interfaceClass = GradeService.class, version = "1.0.0", timeout = 10000)
public class GradeServiceImpl implements GradeService {
    @Resource
    private GradeMapper gradeMapper;

    @Resource
    private SnowFlake snowFlake;

    /**
     * 获取所有年级信息的业务方法
     *
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.entity.Grade>
     * @author h0ss
     */
    @Override
    public List<Grade> getAll() {
        GradeExample gradeExample = new GradeExample();
        gradeExample.setOrderByClause("`order`");
        return gradeMapper.selectByExample(gradeExample);
    }

    /**
     * 新增/编辑年级信息的业务方法 由于年级信息有限故不设置分页
     *
     * @param gradeSaveReq : 保存的年级信息
     * @author h0ss
     */
    @Override
    public void save(GradeSaveReq gradeSaveReq) {
        Grade grade = new Grade();
        BeanUtils.copyProperties(gradeSaveReq, grade);
        // 判断是新增还是编辑
        if (grade.getId() != null) {
            GradeExample gradeExample = new GradeExample();
            GradeExample.Criteria criteria = gradeExample.createCriteria();
            criteria.andIdEqualTo(grade.getId());
            gradeMapper.updateByExampleSelective(grade, gradeExample);

        } else {
            // 雪花算法生成id
            grade.setId(snowFlake.nextId());
            gradeMapper.insert(grade);
        }
    }

    /**
     * 删除年级信息的业务方法
     *
     * @param id : 要删除的年级id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        GradeExample gradeExample = new GradeExample();
        GradeExample.Criteria criteria = gradeExample.createCriteria();
        criteria.andIdEqualTo(id);
        gradeMapper.deleteByExample(gradeExample);
    }

}
