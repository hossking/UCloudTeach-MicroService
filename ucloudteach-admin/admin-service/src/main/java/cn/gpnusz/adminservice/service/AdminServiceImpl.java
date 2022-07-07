package cn.gpnusz.adminservice.service;


import cn.dev33.satoken.stp.StpUtil;
import cn.gpnusz.adminservice.entity.AdminExample;
import cn.gpnusz.adminservice.mapper.AdminMapper;
import cn.gpnusz.adminservice.util.SnowFlake;
import cn.gpnusz.ucloudteachadmin.service.AdminService;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachcommon.util.RandomKeyUtil;
import cn.gpnusz.ucloudteachentity.common.PageReq;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.Admin;
import cn.gpnusz.ucloudteachentity.req.AdminSaveReq;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tencentcloudapi.vpc.v20170312.models.Ip6RuleInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author h0ss
 * @description 管理admin的业务层
 * @date 2021/11/23 - 21:26
 */
@DubboService(interfaceClass = AdminService.class, version = "1.0.0", timeout = 10000)
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedissonClient redissonClient;

    private static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

    /**
     * 查询管理员信息的业务方法
     *
     * @param pageReq : 查询（分页）参数
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.Admin>
     * @author h0ss
     */
    @Override
    public PageResp<Admin> getAll(PageReq pageReq) {
        // 获取全部学员信息每次最多显示100条
        if (pageReq.getPage() != null && pageReq.getSize() != null) {
            PageHelper.startPage(pageReq.getPage(), pageReq.getSize());
        } else {
            PageHelper.startPage(1, 100);
        }
        List<Admin> adminList = adminMapper.selectByExample(null);
        PageResp<Admin> pageResp = PageInfoUtil.getPageInfoResp(adminList, Admin.class);
        LOG.info("总记录:{}", pageResp != null ? pageResp.getTotal() : 0);
        return pageResp;
    }

    /**
     * 新增或编辑管理员信息的业务方法
     *
     * @param adminSaveReq : 保存的管理员信息数据
     * @author h0ss
     */
    @Override
    public void save(AdminSaveReq adminSaveReq) {
        // 获取随机盐值
        String salt = RandomKeyUtil.getRandomSalt(6);
        // 密码+盐 MD5加密处理
        String passwd = adminSaveReq.getPassword() + salt;
        adminSaveReq.setPassword(DigestUtils.md5DigestAsHex(passwd.getBytes(StandardCharsets.UTF_8)));
        // 创建一个新对象
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminSaveReq, admin);
        admin.setSalt(salt);
        // 判断是新增还是编辑
        if (admin.getId() != null) {
            admin.setPassword(null);
            admin.setSalt(null);
            AdminExample adminExample = new AdminExample();
            AdminExample.Criteria criteria = adminExample.createCriteria();
            criteria.andIdEqualTo(admin.getId());
            adminMapper.updateByExampleSelective(admin, adminExample);
        } else {
            // 雪花算法生成id
            admin.setId(snowFlake.nextId());
            adminMapper.insert(admin);
            // 新增用户时需要写入布隆过滤器
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("admin:username:bl");
            bloomFilter.tryInit(10000L, 0.01);
            bloomFilter.add(admin.getUsername());
            LOG.info("用户 {} 写入布隆过滤器成功", admin.getUsername());
            LOG.info("用户 {} 创建成功", admin.getUsername());
        }
    }


    /**
     * 删除管理员的业务方法
     *
     * @param id : 要删除的管理员id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        if (!id.equals(StpUtil.getLoginIdAsLong())) {
            AdminExample adminExample = new AdminExample();
            AdminExample.Criteria criteria = adminExample.createCriteria();
            criteria.andIdEqualTo(id);
            adminMapper.deleteByExample(adminExample);
        }
    }

    /**
     * 根据id获取管理员信息
     *
     * @param id : 管理员id
     * @return : cn.gpnusz.ucloudteachentity.entity.Admin
     * @author h0ss
     */
    @Override
    public Admin getInfoById(Long id) {
        AdminExample adminExample = new AdminExample();
        AdminExample.Criteria criteria = adminExample.createCriteria();
        criteria.andIdEqualTo(id);
        List<Admin> adminList = adminMapper.selectByExample(adminExample);
        if (adminList.isEmpty()) {
            return null;
        } else {
            return adminList.get(0);
        }
    }
}
