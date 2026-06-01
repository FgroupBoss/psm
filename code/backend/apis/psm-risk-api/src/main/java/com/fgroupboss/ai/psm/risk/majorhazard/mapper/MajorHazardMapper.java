package com.fgroupboss.ai.psm.risk.majorhazard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.risk.majorhazard.model.entity.MajorHazardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MajorHazardMapper extends BaseMapper<MajorHazardEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("keyword") String keyword,
                       @Param("status") String status,
                       @Param("level") String level);

    List<MajorHazardEntity> listByTenant(@Param("tenantId") Long tenantId,
                                         @Param("keyword") String keyword,
                                         @Param("status") String status,
                                         @Param("level") String level,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    MajorHazardEntity findByHazardNo(@Param("tenantId") Long tenantId, @Param("hazardNo") String hazardNo);

    List<MajorHazardEntity> listForRiskContext(@Param("tenantId") Long tenantId,
                                               @Param("areaId") Long areaId,
                                               @Param("unitId") Long unitId,
                                               @Param("pointIds") List<Long> pointIds);
}
