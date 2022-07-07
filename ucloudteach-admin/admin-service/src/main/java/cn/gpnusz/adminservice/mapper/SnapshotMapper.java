package cn.gpnusz.adminservice.mapper;

import cn.gpnusz.adminservice.entity.SnapshotExample;
import cn.gpnusz.ucloudteachentity.entity.Snapshot;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnapshotMapper {
    long countByExample(SnapshotExample example);

    int deleteByExample(SnapshotExample example);

    int insert(Snapshot record);

    int insertSelective(Snapshot record);

    List<Snapshot> selectByExample(SnapshotExample example);

    int updateByExampleSelective(@Param("record") Snapshot record, @Param("example") SnapshotExample example);

    int updateByExample(@Param("record") Snapshot record, @Param("example") SnapshotExample example);
}