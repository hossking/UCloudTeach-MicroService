package cn.gpnusz.examservice.mapper;

import cn.gpnusz.examservice.entity.ExamInfoExample;
import cn.gpnusz.ucloudteachentity.entity.ExamInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExamInfoMapper {
    long countByExample(ExamInfoExample example);

    int deleteByExample(ExamInfoExample example);

    int insert(ExamInfo record);

    int insertSelective(ExamInfo record);

    List<ExamInfo> selectByExampleWithBLOBs(ExamInfoExample example);

    List<ExamInfo> selectByExample(ExamInfoExample example);

    int updateByExampleSelective(@Param("record") ExamInfo record, @Param("example") ExamInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") ExamInfo record, @Param("example") ExamInfoExample example);

    int updateByExample(@Param("record") ExamInfo record, @Param("example") ExamInfoExample example);
}