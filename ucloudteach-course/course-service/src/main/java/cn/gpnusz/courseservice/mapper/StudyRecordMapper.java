package cn.gpnusz.courseservice.mapper;

import cn.gpnusz.courseservice.entity.StudyRecordExample;
import cn.gpnusz.ucloudteachentity.entity.StudyRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudyRecordMapper {
    long countByExample(StudyRecordExample example);

    int deleteByExample(StudyRecordExample example);

    int insert(StudyRecord record);

    int insertSelective(StudyRecord record);

    List<StudyRecord> selectByExample(StudyRecordExample example);

    int updateByExampleSelective(@Param("record") StudyRecord record, @Param("example") StudyRecordExample example);

    int updateByExample(@Param("record") StudyRecord record, @Param("example") StudyRecordExample example);
}