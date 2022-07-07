package cn.gpnusz.courseservice.mapper;

import cn.gpnusz.courseservice.entity.CourseRecordExample;
import cn.gpnusz.ucloudteachentity.entity.CourseRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseRecordMapper {
    long countByExample(CourseRecordExample example);

    int deleteByExample(CourseRecordExample example);

    int insert(CourseRecord record);

    int insertSelective(CourseRecord record);

    List<CourseRecord> selectByExample(CourseRecordExample example);

    int updateByExampleSelective(@Param("record") CourseRecord record, @Param("example") CourseRecordExample example);

    int updateByExample(@Param("record") CourseRecord record, @Param("example") CourseRecordExample example);
}