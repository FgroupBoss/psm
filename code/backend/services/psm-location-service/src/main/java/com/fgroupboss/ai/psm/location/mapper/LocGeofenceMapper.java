package com.fgroupboss.ai.psm.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.location.model.entity.LocGeofenceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LocGeofenceMapper extends BaseMapper<LocGeofenceEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("keyword") String keyword,
                       @Param("fenceType") String fenceType);

    List<LocGeofenceEntity> listByTenant(@Param("tenantId") Long tenantId,
                                         @Param("keyword") String keyword,
                                         @Param("fenceType") String fenceType,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    LocGeofenceEntity findByCode(@Param("tenantId") Long tenantId, @Param("fenceCode") String fenceCode);
}
