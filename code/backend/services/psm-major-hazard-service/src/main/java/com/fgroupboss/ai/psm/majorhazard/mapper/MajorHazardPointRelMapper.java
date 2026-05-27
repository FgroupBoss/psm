package com.fgroupboss.ai.psm.majorhazard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardPointRelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MajorHazardPointRelMapper extends BaseMapper<MajorHazardPointRelEntity> {

    List<MajorHazardPointRelEntity> listByHazardId(@Param("tenantId") Long tenantId, @Param("hazardId") Long hazardId);

    MajorHazardPointRelEntity findActive(@Param("tenantId") Long tenantId,
                                         @Param("hazardId") Long hazardId,
                                         @Param("monitorPointId") Long monitorPointId);
}
