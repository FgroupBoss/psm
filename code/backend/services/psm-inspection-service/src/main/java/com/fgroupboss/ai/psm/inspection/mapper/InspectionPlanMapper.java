package com.fgroupboss.ai.psm.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.inspection.model.entity.InspectionPlanEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InspectionPlanMapper extends BaseMapper<InspectionPlanEntity> {

    long countByTenant(@Param("tenantId") Long tenantId, @Param("keyword") String keyword);

    List<InspectionPlanEntity> listByTenant(@Param("tenantId") Long tenantId,
                                          @Param("keyword") String keyword,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);

    InspectionPlanEntity findByCode(@Param("tenantId") Long tenantId, @Param("planCode") String planCode);

    List<InspectionPlanEntity> listEnabled();
}
