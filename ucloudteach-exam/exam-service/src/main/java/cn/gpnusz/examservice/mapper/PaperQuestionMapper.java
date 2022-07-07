package cn.gpnusz.examservice.mapper;


import cn.gpnusz.examservice.entity.PaperQuestionExample;
import cn.gpnusz.ucloudteachentity.entity.PaperQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PaperQuestionMapper {
    long countByExample(PaperQuestionExample example);

    int deleteByExample(PaperQuestionExample example);

    int insert(PaperQuestion record);

    int insertSelective(PaperQuestion record);

    List<PaperQuestion> selectByExample(PaperQuestionExample example);

    int updateByExampleSelective(@Param("record") PaperQuestion record, @Param("example") PaperQuestionExample example);

    int updateByExample(@Param("record") PaperQuestion record, @Param("example") PaperQuestionExample example);
}