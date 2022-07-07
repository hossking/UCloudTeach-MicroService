package cn.gpnusz.courseservice.mapper;

import cn.gpnusz.courseservice.entity.CoursePeriodExample;
import cn.gpnusz.ucloudteachentity.entity.CoursePeriod;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CoursePeriodMapper {
    long countByExample(CoursePeriodExample example);

    int deleteByExample(CoursePeriodExample example);

    int insert(CoursePeriod record);

    int insertSelective(CoursePeriod record);

    List<CoursePeriod> selectByExampleWithBLOBs(CoursePeriodExample example);

    List<CoursePeriod> selectByExample(CoursePeriodExample example);

    int updateByExampleSelective(@Param("record") CoursePeriod record, @Param("example") CoursePeriodExample example);

    int updateByExampleWithBLOBs(@Param("record") CoursePeriod record, @Param("example") CoursePeriodExample example);

    int updateByExample(@Param("record") CoursePeriod record, @Param("example") CoursePeriodExample example);
}