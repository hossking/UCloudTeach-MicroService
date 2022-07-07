package cn.gpnusz.customservice.service;

import cn.gpnusz.custominterface.service.GridConfigService;
import cn.gpnusz.customservice.entity.GridConfigExample;
import cn.gpnusz.customservice.mapper.GridConfigMapper;
import cn.gpnusz.customservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
import cn.gpnusz.ucloudteachcommon.util.RedisOpsUtil;
import cn.gpnusz.ucloudteachentity.entity.GridConfig;
import cn.gpnusz.ucloudteachentity.req.GridConfigSaveReq;
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
 * @description 操作菜单项的业务层
 * @date 2021/11/21 14:57
 */
@DubboService(version = "1.0.0", timeout = 10000, interfaceClass = GridConfigService.class)
public class GridConfigServiceImpl implements GridConfigService {
    @Resource
    private GridConfigMapper gridConfigMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取全部菜单项配置的业务方法
     *
     * @return : java.util.List<cn.gpnusz.ucloudteach.entity.GridConfig>
     * @author h0ss
     */
    @Override
    public List<GridConfig> getAll() {
        // 优先从Redis取出 按照sort值排序
        Set<String> grids = stringRedisTemplate.opsForZSet().range(RedisKeyUtil.ZSET_KEY_GRID, 0, -1);
        List<GridConfig> gridList = RedisOpsUtil.dumpZset(grids, GridConfig.class);
        if (ObjectUtils.isEmpty(gridList)) {
            // 数据不在缓存中
            GridConfigExample gridConfigExample = new GridConfigExample();
            gridConfigExample.setOrderByClause("sort");
            gridList = gridConfigMapper.selectByExample(gridConfigExample);
            // 存redis
            for (GridConfig grid : gridList) {
                stringRedisTemplate.opsForZSet()
                        .add(RedisKeyUtil.ZSET_KEY_GRID, JSON.toJSONString(grid), grid.getSort());
            }
        }
        return gridList;

    }

    /**
     * 保存菜单项配置的业务方法
     *
     * @param gridConfigSaveReq : 保存的对象
     * @author h0ss
     */
    @Override
    public void save(GridConfigSaveReq gridConfigSaveReq) {
        GridConfig gridConfig = new GridConfig();
        BeanUtils.copyProperties(gridConfigSaveReq, gridConfig);
        if (gridConfig.getId() != null) {
            GridConfigExample gridConfigExample = new GridConfigExample();
            GridConfigExample.Criteria criteria = gridConfigExample.createCriteria();
            criteria.andIdEqualTo(gridConfig.getId());
            List<GridConfig> gridSelect = gridConfigMapper.selectByExample(gridConfigExample);
            // 写新数据
            gridConfigMapper.updateByExampleSelective(gridConfig, gridConfigExample);
            // 清旧缓存
            stringRedisTemplate
                    .opsForZSet()
                    .remove(RedisKeyUtil.ZSET_KEY_GRID, JSON.toJSONString(gridSelect));
        } else {
            // 雪花算法生成id
            gridConfig.setId(snowFlake.nextId());
            gridConfigMapper.insert(gridConfig);
        }
        // 写完数据库后写入缓存
        stringRedisTemplate
                .opsForZSet()
                .add(RedisKeyUtil.ZSET_KEY_GRID, JSON.toJSONString(gridConfig), gridConfig.getSort());
    }

    /**
     * 根据id删除菜单项配置信息的业务方法
     *
     * @param id : 菜单项配置id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        if (id != null) {
            GridConfigExample gridConfigExample = new GridConfigExample();
            GridConfigExample.Criteria criteria = gridConfigExample.createCriteria();
            criteria.andIdEqualTo(id);
            List<GridConfig> gridConfig = gridConfigMapper.selectByExample(gridConfigExample);
            // 删数据库数据库
            gridConfigMapper.deleteByExample(gridConfigExample);
            // 删缓存
            stringRedisTemplate
                    .opsForZSet()
                    .remove(RedisKeyUtil.ZSET_KEY_GRID, JSON.toJSONString(gridConfig.get(0)));
        }
    }
}