package cn.gpnusz.courseservice.service;

import cn.gpnusz.courseinterface.service.SubjectService;
import cn.gpnusz.courseservice.entity.SubjectExample;
import cn.gpnusz.courseservice.mapper.SubjectMapper;
import cn.gpnusz.courseservice.util.SnowFlake;
import cn.gpnusz.ucloudteachentity.entity.Subject;
import cn.gpnusz.ucloudteachentity.req.SubjectSaveReq;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author h0ss
 * @description 科目信息业务层
 * @date 2021/11/14 0:22
 */

@Service
@DubboService(interfaceClass = SubjectService.class, version = "1.0.0", timeout = 10000)
public class SubjectServiceImpl implements SubjectService {
    @Resource
    private SubjectMapper subjectMapper;

    @Resource
    private SnowFlake snowFlake;

    /**
     * 获取全部科目信息的业务方法
     *
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.entity.Subject>
     * @author h0ss
     */
    @Override
    public List<Subject> getAll() {
        SubjectExample subjectExample = new SubjectExample();
        return subjectMapper.selectByExample(subjectExample);
    }

    /**
     * 根据年级id获取科目信息
     *
     * @param gradeId : 年级id
     * @return : java.util.List<cn.gpnusz.ucloudteachentity.entity.Subject>
     * @author h0ss
     */
    @Override
    public List<Subject> getByGrade(Long gradeId) {
        SubjectExample subjectExample = new SubjectExample();
        SubjectExample.Criteria criteria = subjectExample.createCriteria();
        criteria.andGradeIdEqualTo(gradeId);
        return subjectMapper.selectByExample(subjectExample);
    }

    /**
     * 新增或编辑科目信息的业务方法
     *
     * @param subjectSaveReq : 保存的科目信息
     * @author h0ss
     */
    @Override
    public void save(SubjectSaveReq subjectSaveReq) {
        Subject subject = new Subject();
        BeanUtils.copyProperties(subjectSaveReq, subject);
        // 判断是新增还是编辑
        if (subject.getId() != null) {
            // 直接更新数据
            SubjectExample subjectExample = new SubjectExample();
            SubjectExample.Criteria criteria = subjectExample.createCriteria();
            criteria.andIdEqualTo(subject.getId());
            subjectMapper.updateByExample(subject, subjectExample);
        } else {
            // 先使用雪花算法生成id后插入表中
            subject.setId(snowFlake.nextId());
            subjectMapper.insert(subject);
        }
    }

    /**
     * 删除科目信息的业务方法
     *
     * @param id : 要删除的科目id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        // 匹配id直接删除
        SubjectExample subjectExample = new SubjectExample();
        SubjectExample.Criteria criteria = subjectExample.createCriteria();
        criteria.andIdEqualTo(id);
        subjectMapper.deleteByExample(subjectExample);
    }
}
