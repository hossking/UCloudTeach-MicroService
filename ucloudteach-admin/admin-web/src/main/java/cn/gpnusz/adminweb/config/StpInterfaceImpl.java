package cn.gpnusz.adminweb.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.gpnusz.ucloudteachadmin.service.AdminService;
import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
import cn.gpnusz.ucloudteachentity.entity.Admin;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author h0ss
 * @description 自定义权限验证接口扩展
 * @date 2021/11/23 - 12:08
 */

@Component
public class StpInterfaceImpl implements StpInterface {

    @DubboReference(version = "1.0.0")
    private AdminService adminService;

    @Resource
    private StringRedisTemplate srt;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String key = RedisKeyUtil.getRoleListKey((String) loginId);
        // 从redis获取权限集合
        Set<String> roleList = srt.opsForSet().members(key);
        if (roleList != null && !roleList.isEmpty()) {
            return new ArrayList<>(roleList);
        }
        // 从数据库获取权限集合
        List<String> roles = new ArrayList<>(3);
        Admin adminInfo = adminService.getInfoById(Long.parseLong((String) loginId));
        if (adminInfo == null) {
            return roles;
        } else {
            srt.opsForSet().add(key, "admin");
            roles.add("admin");
            srt.opsForSet().add(key, "user");
            roles.add("user");
            if (adminInfo.getSuperFlag()) {
                srt.opsForSet().add(key, "superAdmin");
                roles.add("superAdmin");
            }
        }
        return roles;
    }
}
