package cn.gpnusz.courseservice.mapper;

import cn.gpnusz.courseservice.entity.CourseCommentExample;
import cn.gpnusz.ucloudteachentity.entity.CourseComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseCommentMapper {
    long countByExample(CourseCommentExample example);

    int deleteByExample(CourseCommentExample example);

    int insert(CourseComment record);

    int insertSelective(CourseComment record);

    List<CourseComment> selectByExampleWithBLOBs(CourseCommentExample example);

    List<CourseComment> selectByExample(CourseCommentExample example);

    int updateByExampleSelective(@Param("record") CourseComment record, @Param("example") CourseCommentExample example);

    int updateByExampleWithBLOBs(@Param("record") CourseComment record, @Param("example") CourseCommentExample example);

    int updateByExample(@Param("record") CourseComment record, @Param("example") CourseCommentExample example);
}