package cn.gpnusz.userservice.service;

import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachcommon.util.RandomKeyUtil;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.StudentInfo;
import cn.gpnusz.ucloudteachentity.exception.BusinessException;
import cn.gpnusz.ucloudteachentity.exception.BusinessExceptionCode;
import cn.gpnusz.ucloudteachentity.req.StuResetPasswordReq;
import cn.gpnusz.ucloudteachentity.req.StudentInfoQueryReq;
import cn.gpnusz.ucloudteachentity.req.StudentInfoSaveReq;
import cn.gpnusz.userinterface.service.StudentInfoService;
import cn.gpnusz.userservice.entity.StudentInfoExample;
import cn.gpnusz.userservice.mapper.StudentInfoMapper;
import cn.gpnusz.userservice.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author h0ss
 * @description 学员信息系统业务层
 * @date 2021/11/12 - 20:57
 */
@DubboService(interfaceClass = StudentInfoService.class, version = "1.0.0", timeout = 10000)
public class StudentInfoServiceImpl implements StudentInfoService {

    @Resource
    private StudentInfoMapper studentInfoMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedissonClient redissonClient;

    private static final Logger LOG = LoggerFactory.getLogger(StudentInfoServiceImpl.class);

    /**
     * 按传入条件查询学员信息的业务方法
     *
     * @param studentInfoQueryReq : 查询条件参数
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.StudentInfo>
     * @author h0ss
     */
    @Override
    public PageResp<StudentInfo> getAllByCondition(StudentInfoQueryReq studentInfoQueryReq) {
        StudentInfoExample studentInfoExample = new StudentInfoExample();
        StudentInfoExample.Criteria criteria = studentInfoExample.createCriteria();
        if (!ObjectUtils.isEmpty(studentInfoQueryReq.getName())) {
            criteria.andNameLike("%" + studentInfoQueryReq.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(studentInfoQueryReq.getDisableFlag())) {
            criteria.andDisableFlagEqualTo(studentInfoQueryReq.getDisableFlag());
        }
        if (!ObjectUtils.isEmpty(studentInfoQueryReq.getGender())) {
            criteria.andGenderEqualTo(studentInfoQueryReq.getGender());
        }
        if (studentInfoQueryReq.getPage() != null && studentInfoQueryReq.getSize() != null) {
            PageHelper.startPage(studentInfoQueryReq.getPage(), studentInfoQueryReq.getSize());
        } else {
            PageHelper.startPage(1, 10);
        }
        List<StudentInfo> studentInfoList = studentInfoMapper.selectByExample(studentInfoExample);
        return PageInfoUtil.getPageInfoResp(studentInfoList, StudentInfo.class);
    }

    /**
     * 查询全部学员信息数据的业务方法
     *
     * @param studentInfoQueryReq : 查询（分页）参数
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.StudentInfo>
     * @author h0ss
     */
    @Override
    public PageResp<StudentInfo> getAll(StudentInfoQueryReq studentInfoQueryReq) {
        // 获取全部学员信息每次最多显示100条
        if (studentInfoQueryReq.getPage() != null && studentInfoQueryReq.getSize() != null) {
            PageHelper.startPage(studentInfoQueryReq.getPage(), studentInfoQueryReq.getSize());
        } else {
            PageHelper.startPage(1, 100);
        }
        List<StudentInfo> studentInfoList = studentInfoMapper.selectByExample(null);
        return PageInfoUtil.getPageInfoResp(studentInfoList, StudentInfo.class);
    }

    /**
     * 新增或编辑学员信息的业务方法
     *
     * @param studentInfoSaveReq : 保存的学员信息数据
     * @author h0ss
     */
    @Override
    public void save(StudentInfoSaveReq studentInfoSaveReq) {
        // 获取随机盐值
        String salt = RandomKeyUtil.getRandomSalt(6);
        // 密码+盐 MD5加密处理
        String passwd = studentInfoSaveReq.getPassword() + salt;
        studentInfoSaveReq.setPassword(DigestUtils.md5DigestAsHex(passwd.getBytes(StandardCharsets.UTF_8)));
        // 创建一个新对象
        StudentInfo studentInfo = new StudentInfo();
        BeanUtils.copyProperties(studentInfoSaveReq, studentInfo);
        studentInfo.setSalt(salt);
        // 判断是新增还是编辑
        if (studentInfo.getId() != null) {
            // 编辑时字段置为null可避免对特殊字段的修改
            studentInfo.setPassword(null);
            studentInfo.setPhone(null);
            studentInfo.setSalt(null);
            StudentInfoExample studentInfoExample = new StudentInfoExample();
            StudentInfoExample.Criteria criteria = studentInfoExample.createCriteria();
            criteria.andIdEqualTo(studentInfo.getId());
            studentInfoMapper.updateByExampleSelective(studentInfo, studentInfoExample);
        } else {
            // 新增时先判断手机号是否存在 不存在全量写入即可
            StudentInfo studentInfoByDb = selectByPhone(studentInfoSaveReq.getPhone());
            if (ObjectUtils.isEmpty(studentInfoByDb)) {
                // 雪花算法生成id
                studentInfo.setId(snowFlake.nextId());
                // 初始化状态
                studentInfo.setDisableFlag(Boolean.FALSE);
                studentInfoMapper.insert(studentInfo);
                String userPhone = studentInfo.getPhone();
                // 新增用户时需要写入布隆过滤器
                RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("student:username:bl");
                bloomFilter.tryInit(100000L, 0.01);
                bloomFilter.add(userPhone);
                LOG.info("用户 {} 写入布隆过滤器成功", userPhone);
                LOG.info("用户 {} 创建成功", userPhone);
            } else {
                // 手机号已存在 抛出自定义的业务异常
                throw new BusinessException(BusinessExceptionCode.PHONE_NUMBER_EXIST);
            }
        }
    }


    /**
     * 重置学员密码的操作
     *
     * @param stuResetPasswordReq : 重置密码对象
     * @author h0ss
     */
    @Override
    public void resetPassword(StuResetPasswordReq stuResetPasswordReq) {
        StudentInfoExample studentInfoExample = new StudentInfoExample();
        StudentInfoExample.Criteria criteria = studentInfoExample.createCriteria();
        // 根据id匹配
        criteria.andIdEqualTo(stuResetPasswordReq.getId());
        // 新建学员信息对象
        StudentInfo studentInfo = new StudentInfo();
        // 从数据库中查出该id对应的用户信息
        List<StudentInfo> stuInfoByDb = studentInfoMapper.selectByExample(studentInfoExample);
        if (!stuInfoByDb.isEmpty()) {
            BeanUtils.copyProperties(stuInfoByDb.get(0), studentInfo);
        } else {
            throw new BusinessException(BusinessExceptionCode.STUDENT_NOT_EXIST);
        }
        // 获取随机盐值
        String salt = RandomKeyUtil.getRandomSalt(6);
        // 密码MD5+盐加密处理
        String passwd = stuResetPasswordReq.getPassword() + salt;
        stuResetPasswordReq.setPassword(DigestUtils.md5DigestAsHex(passwd.getBytes(StandardCharsets.UTF_8)));
        // 只更改密码与盐值
        studentInfo.setPassword(stuResetPasswordReq.getPassword());
        studentInfo.setSalt(salt);
        studentInfoMapper.updateByExampleSelective(studentInfo, studentInfoExample);
    }

    /**
     * 封禁/解封学员的业务方法
     *
     * @param id   : 学员id
     * @param flag : 封禁为1 解封为0
     * @author h0ss
     */
    @Override
    public void banStudent(Long id, Boolean flag) {
        StudentInfoExample studentInfoExample = new StudentInfoExample();
        StudentInfoExample.Criteria criteria = studentInfoExample.createCriteria();
        criteria.andIdEqualTo(id);
        List<StudentInfo> infoList = studentInfoMapper.selectByExample(studentInfoExample);
        if (!infoList.isEmpty()) {
            StudentInfo studentInfo = infoList.get(0);
            studentInfo.setDisableFlag(flag);
            studentInfoMapper.updateByExampleSelective(studentInfo, studentInfoExample);
        } else {
            throw new BusinessException(BusinessExceptionCode.STUDENT_NOT_EXIST);
        }
    }

    /**
     * 删除学员信息的业务方法
     *
     * @param id : 要删除的学员信息id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        StudentInfoExample studentInfoExample = new StudentInfoExample();
        StudentInfoExample.Criteria criteria = studentInfoExample.createCriteria();
        criteria.andIdEqualTo(id);
        studentInfoMapper.deleteByExample(studentInfoExample);
        LOG.info("学员 {} 被删除", id);
    }

    /**
     * 按传入的手机号查找对应的学员信息
     *
     * @param phone : 手机号
     * @return : cn.gpnusz.ucloudteachentity.entity.StudentInfo
     * @author h0ss
     */
    @Override
    public StudentInfo selectByPhone(String phone) {
        StudentInfoExample studentInfoExample = new StudentInfoExample();
        StudentInfoExample.Criteria criteria = studentInfoExample.createCriteria();
        criteria.andPhoneEqualTo(phone);
        List<StudentInfo> studentInfoList = studentInfoMapper.selectByExample(studentInfoExample);
        if (!studentInfoList.isEmpty()) {
            return studentInfoList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取学生数量
     *
     * @return : java.lang.Long
     * @author h0ss
     */
    @Override
    public Long getStuCount() {
        return studentInfoMapper.countByExample(null);
    }
}
