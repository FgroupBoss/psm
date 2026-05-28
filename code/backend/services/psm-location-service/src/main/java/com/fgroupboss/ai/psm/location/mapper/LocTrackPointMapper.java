package com.fgroupboss.ai.psm.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.location.model.entity.LocTrackPointEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LocTrackPointMapper extends BaseMapper<LocTrackPointEntity> {

    List<LocTrackPointEntity> listTracks(@Param("tenantId") Long tenantId,
                                         @Param("tagNo") String tagNo,
                                         @Param("personId") Long personId,
                                         @Param("areaId") Long areaId,
                                         @Param("fromTime") LocalDateTime fromTime,
                                         @Param("toTime") LocalDateTime toTime,
                                         @Param("limit") int limit);
}
