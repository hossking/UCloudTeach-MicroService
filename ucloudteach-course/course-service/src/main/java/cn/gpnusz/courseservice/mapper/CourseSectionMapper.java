package cn.gpnusz.courseservice.mapper;


import cn.gpnusz.courseservice.entity.CourseSectionExample;
import cn.gpnusz.ucloudteachentity.entity.CourseSection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseSectionMapper {
    long countByExample(CourseSectionExample example);

    int deleteByExample(CourseSectionExample example);

    int insert(CourseSection record);

    int insertSelective(CourseSection record);

    List<CourseSection> selectByExample(CourseSectionExample example);

    int updateByExampleSelective(@Param("record") CourseSection record, @Param("example") CourseSectionExample example);

    int updateByExample(@Param("record") CourseSection record, @Param("example") CourseSectionExample example);
}