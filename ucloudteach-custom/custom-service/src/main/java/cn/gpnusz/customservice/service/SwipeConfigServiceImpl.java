package cn.gpnusz.customservice.service;


import cn.gpnusz.custominterface.service.SwipeConfigService;
import cn.gpnusz.customservice.entity.SwipeConfigExample;
import cn.gpnusz.customservice.mapper.SwipeConfigMapper;
import cn.gpnusz.customservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
import cn.gpnusz.ucloudteachcommon.util.RedisOpsUtil;
import cn.gpnusz.ucloudteachentity.entity.SwipeConfig;
import cn.gpnusz.ucloudteachentity.req.SwipeConfigSaveReq;
import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author h0ss
 * @description 操作轮播图配置的业务层
 * @date 2021/11/21 14:55
 */
@DubboService(version = "1.0.0", timeout = 10000, interfaceClass = SwipeConfigService.class)
public class SwipeConfigServiceImpl implements SwipeConfigService {
    @Resource
    private SwipeConfigMapper swipeConfigMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 获取全部轮播图配置的业务方法
     *
     * @return : java.util.List<cn.gpnusz.ucloudteach.entity.SwipeConfig>
     * @author h0ss
     */
    @Override
    public List<SwipeConfig> getAll() {
        // 优先从Redis取出 按照sort值排序
        Set<String> swipes = stringRedisTemplate.opsForZSet().range(RedisKeyUtil.ZSET_KEY_SWIPE, 0, -1);
        List<SwipeConfig> swipeList = RedisOpsUtil.dumpZset(swipes, SwipeConfig.class);
        if (ObjectUtils.isEmpty(swipeList)) {
            // 从数据库取
            SwipeConfigExample swipeConfigExample = new SwipeConfigExample();
            swipeConfigExample.setOrderByClause("sort");
            swipeList = swipeConfigMapper.selectByExample(swipeConfigExample);
            // 写入缓存
            for (SwipeConfig swipe : swipeList) {
                stringRedisTemplate.opsForZSet()
                        .add(RedisKeyUtil.ZSET_KEY_SWIPE, JSON.toJSONString(swipe), swipe.getSort());
            }
        }
        return swipeList;
    }

    /**
     * 保存轮播图配置的业务方法
     *
     * @param swipeConfigSaveReq : 保存的对象
     * @author h0ss
     */
    @Override
    public void save(SwipeConfigSaveReq swipeConfigSaveReq) {
        SwipeConfig swipeConfig = new SwipeConfig();
        BeanUtils.copyProperties(swipeConfigSaveReq, swipeConfig);
        // 编辑操作
        if (swipeConfig.getId() != null) {
            SwipeConfigExample swipeConfigExample = new SwipeConfigExample();
            SwipeConfigExample.Criteria criteria = swipeConfigExample.createCriteria();
            criteria.andIdEqualTo(swipeConfig.getId());
            // 先写数据库 后清除缓存
            List<SwipeConfig> swipeSelect = swipeConfigMapper.selectByExample(swipeConfigExample);
            // 写新数据
            swipeConfigMapper.updateByExampleSelective(swipeConfig, swipeConfigExample);
            // 清旧缓存
            if (!ObjectUtils.isEmpty(swipeConfig)) {
                stringRedisTemplate
                        .opsForZSet()
                        .remove(RedisKeyUtil.ZSET_KEY_SWIPE, JSON.toJSONString(swipeSelect));
            }
        } else {
            // 雪花算法生成id
            swipeConfig.setId(snowFlake.nextId());
            swipeConfigMapper.insert(swipeConfig);
        }
        // 写完数据库后写入缓存
        stringRedisTemplate
                .opsForZSet()
                .add(RedisKeyUtil.ZSET_KEY_SWIPE, JSON.toJSONString(swipeConfig), swipeConfig.getSort());

    }

    /**
     * 根据id删除轮播图配置信息的业务方法
     *
     * @param id : 轮播图配置id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        if (id != null) {
            SwipeConfigExample swipeConfigExample = new SwipeConfigExample();
            SwipeConfigExample.Criteria criteria = swipeConfigExample.createCriteria();
            criteria.andIdEqualTo(id);
            // 获取数据
            List<SwipeConfig> swipeConfig = swipeConfigMapper.selectByExample(swipeConfigExample);
            swipeConfigMapper.deleteByExample(swipeConfigExample);
            // 删除数据库键值之后删除缓存
            if (ObjectUtils.isEmpty(swipeConfig)) {
                stringRedisTemplate
                        .opsForZSet()
                        .remove(RedisKeyUtil.ZSET_KEY_SWIPE, JSON.toJSONString(swipeConfig.get(0)));
            }

        }
    }
}