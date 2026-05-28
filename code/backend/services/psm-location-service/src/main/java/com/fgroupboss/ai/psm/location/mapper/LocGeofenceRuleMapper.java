package com.fgroupboss.ai.psm.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.location.model.entity.LocGeofenceRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LocGeofenceRuleMapper extends BaseMapper<LocGeofenceRuleEntity> {

    List<LocGeofenceRuleEntity> listByFenceId(@Param("tenantId") Long tenantId, @Param("fenceId") Long fenceId);
}
