package cn.gpnusz.courseservice.mapper;

import cn.gpnusz.courseservice.entity.CourseMemberExample;
import cn.gpnusz.ucloudteachentity.entity.CourseMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseMemberMapper {
    long countByExample(CourseMemberExample example);

    int deleteByExample(CourseMemberExample example);

    int insert(CourseMember record);

    int insertSelective(CourseMember record);

    List<CourseMember> selectByExample(CourseMemberExample example);

    int updateByExampleSelective(@Param("record") CourseMember record, @Param("example") CourseMemberExample example);

    int updateByExample(@Param("record") CourseMember record, @Param("example") CourseMemberExample example);
}