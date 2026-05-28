package com.fgroupboss.ai.psm.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.inspection.model.entity.RoutePointEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoutePointMapper extends BaseMapper<RoutePointEntity> {

    List<RoutePointEntity> listByRoute(@Param("tenantId") Long tenantId, @Param("routeId") Long routeId);

    RoutePointEntity findByIdAndRoute(@Param("tenantId") Long tenantId,
                                      @Param("routeId") Long routeId,
                                      @Param("pointId") Long pointId);

    int softDeleteByRoute(@Param("tenantId") Long tenantId, @Param("routeId") Long routeId);
}
