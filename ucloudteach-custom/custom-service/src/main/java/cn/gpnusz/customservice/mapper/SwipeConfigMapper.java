package cn.gpnusz.customservice.mapper;

import cn.gpnusz.customservice.entity.SwipeConfigExample;
import cn.gpnusz.ucloudteachentity.entity.SwipeConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SwipeConfigMapper {
    long countByExample(SwipeConfigExample example);

    int deleteByExample(SwipeConfigExample example);

    int insert(SwipeConfig record);

    int insertSelective(SwipeConfig record);

    List<SwipeConfig> selectByExample(SwipeConfigExample example);

    int updateByExampleSelective(@Param("record") SwipeConfig record, @Param("example") SwipeConfigExample example);

    int updateByExample(@Param("record") SwipeConfig record, @Param("example") SwipeConfigExample example);
}