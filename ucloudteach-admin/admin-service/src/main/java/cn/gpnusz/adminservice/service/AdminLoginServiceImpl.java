package cn.gpnusz.adminservice.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.gpnusz.adminservice.entity.AdminExample;
import cn.gpnusz.adminservice.mapper.AdminMapper;
import cn.gpnusz.ucloudteachadmin.service.AdminLoginService;
import cn.gpnusz.ucloudteachentity.entity.Admin;
import cn.gpnusz.ucloudteachentity.exception.BusinessException;
import cn.gpnusz.ucloudteachentity.exception.BusinessExceptionCode;
import cn.gpnusz.ucloudteachentity.exception.BusinessException;
import cn.gpnusz.ucloudteachentity.exception.BusinessExceptionCode;
import cn.gpnusz.ucloudteachentity.req.AdminLoginReq;
import cn.gpnusz.ucloudteachentity.resp.AdminLoginResp;
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
 * @description 管理员登录的业务
 * @date 2021/11/23 - 3:15
 */
@DubboService(interfaceClass = AdminLoginService.class, version = "1.0.0", timeout = 10000)
public class AdminLoginServiceImpl implements AdminLoginService {

    private final static Logger LOG = LoggerFactory.getLogger(AdminLoginServiceImpl.class);

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 管理员登录业务方法
     *
     * @param adminLoginReq : 登录信息
     * @return : cn.gpnusz.ucloudteachentity.resp.AdminLoginResp
     * @author h0ss
     */
    @Override
    public AdminLoginResp adminLogin(AdminLoginReq adminLoginReq) {
        // 验证码后端校验 [服务过期 暂时关闭校验功能]
        // Boolean codeCheck = CheckCodeUtils.checkRes(adminLoginReq.getTicket(), adminLoginReq.getRandStr());
        Boolean codeCheck = Boolean.TRUE;
        if (codeCheck && !ObjectUtils.isEmpty(adminLoginReq)) {
            // 先从布隆过滤器中判断是否存在用户[防止恶意缓存穿透]
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("admin:username:bl");
            bloomFilter.tryInit(10000L, 0.01);
            // 预设加入admin
            bloomFilter.add("admin");
            boolean isExistUser = bloomFilter.contains(adminLoginReq.getUsername());
            LOG.info("布隆过滤器判断用户 {} 结果：{}", adminLoginReq.getUsername(), isExistUser);
            if (!isExistUser) {
                throw new BusinessException(BusinessExceptionCode.LOGIN_FAIL);
            }
            // 开始取数据库数据对比
            Admin admin = new Admin();
            BeanUtils.copyProperties(adminLoginReq, admin);
            AdminExample adminExample = new AdminExample();
            AdminExample.Criteria criteria = adminExample.createCriteria();
            criteria.andUsernameEqualTo(admin.getUsername());
            List<Admin> res = adminMapper.selectByExample(adminExample);
            if (!ObjectUtils.isEmpty(res)) {
                // 从数据库获取的用户信息
                Admin adminOfDb = res.get(0);
                String salt = adminOfDb.getSalt();
                String passwdSalt = admin.getPassword() + salt;
                // 用户输入的密码
                String inputPassword = DigestUtils.md5DigestAsHex(passwdSalt.getBytes(StandardCharsets.UTF_8));
                // 对比密码
                if (adminOfDb.getPassword().equals(inputPassword)) {
                    // 框架登录操作
                    StpUtil.login(adminOfDb.getId());
                    SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                    LOG.info("登录token信息 {}", tokenInfo);
                    // 写入返回信息
                    AdminLoginResp resp = new AdminLoginResp();
                    resp.setToken(tokenInfo.getTokenValue());
                    resp.setUsername(adminOfDb.getUsername());
                    resp.setSuperFlag(adminOfDb.getSuperFlag());
                    return resp;
                }
            }
        }
        throw new BusinessException(BusinessExceptionCode.LOGIN_FAIL);
    }


    /**
     * 管理员检查登录状态的业务方法 如果没有登录会直接抛出NotLoginException异常
     *
     * @author h0ss
     */
    @Override
    public void checkLogin() {
        StpUtil.checkLogin();
    }

    /**
     * 管理员退出登陆的业务方法
     *
     * @author h0ss
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }
}
