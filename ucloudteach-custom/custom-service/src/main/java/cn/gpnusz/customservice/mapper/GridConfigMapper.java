package cn.gpnusz.customservice.mapper;

import cn.gpnusz.customservice.entity.GridConfigExample;
import cn.gpnusz.ucloudteachentity.entity.GridConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GridConfigMapper {
    long countByExample(GridConfigExample example);

    int deleteByExample(GridConfigExample example);

    int insert(GridConfig record);

    int insertSelective(GridConfig record);

    List<GridConfig> selectByExample(GridConfigExample example);

    int updateByExampleSelective(@Param("record") GridConfig record, @Param("example") GridConfigExample example);

    int updateByExample(@Param("record") GridConfig record, @Param("example") GridConfigExample example);
}