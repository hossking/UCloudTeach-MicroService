package cn.gpnusz.userservice.mapper;



import cn.gpnusz.ucloudteachentity.entity.StudentInfo;
import cn.gpnusz.userservice.entity.StudentInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentInfoMapper {
    long countByExample(StudentInfoExample example);

    int deleteByExample(StudentInfoExample example);

    int insert(StudentInfo record);

    int insertSelective(StudentInfo record);

    List<StudentInfo> selectByExample(StudentInfoExample example);

    int updateByExampleSelective(@Param("record") StudentInfo record, @Param("example") StudentInfoExample example);

    int updateByExample(@Param("record") StudentInfo record, @Param("example") StudentInfoExample example);
}