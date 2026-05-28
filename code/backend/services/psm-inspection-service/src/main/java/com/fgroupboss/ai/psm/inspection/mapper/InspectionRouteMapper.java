package com.fgroupboss.ai.psm.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.inspection.model.entity.InspectionRouteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InspectionRouteMapper extends BaseMapper<InspectionRouteEntity> {

    long countByTenant(@Param("tenantId") Long tenantId, @Param("keyword") String keyword);

    List<InspectionRouteEntity> listByTenant(@Param("tenantId") Long tenantId,
                                             @Param("keyword") String keyword,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);

    InspectionRouteEntity findByCode(@Param("tenantId") Long tenantId, @Param("routeCode") String routeCode);
}
