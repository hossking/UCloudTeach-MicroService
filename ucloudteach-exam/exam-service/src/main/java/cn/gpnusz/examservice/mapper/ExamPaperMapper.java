package cn.gpnusz.examservice.mapper;


import cn.gpnusz.examservice.entity.ExamPaperExample;
import cn.gpnusz.ucloudteachentity.entity.ExamPaper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExamPaperMapper {
    long countByExample(ExamPaperExample example);

    int deleteByExample(ExamPaperExample example);

    int insert(ExamPaper record);

    int insertSelective(ExamPaper record);

    List<ExamPaper> selectByExample(ExamPaperExample example);

    int updateByExampleSelective(@Param("record") ExamPaper record, @Param("example") ExamPaperExample example);

    int updateByExample(@Param("record") ExamPaper record, @Param("example") ExamPaperExample example);
}