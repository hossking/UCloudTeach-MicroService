package cn.gpnusz.liveservice.mapper;

import cn.gpnusz.liveservice.entity.LiveContentExample;
import java.util.List;

import cn.gpnusz.ucloudteachentity.entity.LiveContent;
import org.apache.ibatis.annotations.Param;

public interface LiveContentMapper {
    long countByExample(LiveContentExample example);

    int deleteByExample(LiveContentExample example);

    int insert(LiveContent record);

    int insertSelective(LiveContent record);

    List<LiveContent> selectByExample(LiveContentExample example);

    int updateByExampleSelective(@Param("record") LiveContent record, @Param("example") LiveContentExample example);

    int updateByExample(@Param("record") LiveContent record, @Param("example") LiveContentExample example);
}