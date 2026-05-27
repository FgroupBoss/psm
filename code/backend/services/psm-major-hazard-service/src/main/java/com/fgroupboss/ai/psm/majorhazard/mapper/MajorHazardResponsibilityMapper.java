package com.fgroupboss.ai.psm.majorhazard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardResponsibilityEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MajorHazardResponsibilityMapper extends BaseMapper<MajorHazardResponsibilityEntity> {

    List<MajorHazardResponsibilityEntity> listByHazardId(@Param("tenantId") Long tenantId,
                                                         @Param("hazardId") Long hazardId);

    MajorHazardResponsibilityEntity findByType(@Param("tenantId") Long tenantId,
                                               @Param("hazardId") Long hazardId,
                                               @Param("responsibilityType") String responsibilityType);
}
