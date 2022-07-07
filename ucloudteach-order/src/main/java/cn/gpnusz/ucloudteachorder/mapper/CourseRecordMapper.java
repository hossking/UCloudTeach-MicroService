package cn.gpnusz.ucloudteachorder.mapper;

import cn.gpnusz.ucloudteachorder.entity.CourseRecord;
import cn.gpnusz.ucloudteachorder.entity.CourseRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CourseRecordMapper {
    long countByExample(CourseRecordExample example);

    int deleteByExample(CourseRecordExample example);

    int insert(CourseRecord record);

    int insertSelective(CourseRecord record);

    List<CourseRecord> selectByExample(CourseRecordExample example);

    int updateByExampleSelective(@Param("record") CourseRecord record, @Param("example") CourseRecordExample example);

    int updateByExample(@Param("record") CourseRecord record, @Param("example") CourseRecordExample example);
}