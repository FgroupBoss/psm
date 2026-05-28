package com.fgroupboss.ai.psm.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.inspection.model.entity.InspectionTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface InspectionTaskMapper extends BaseMapper<InspectionTaskEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("status") String status,
                       @Param("executorId") Long executorId);

    List<InspectionTaskEntity> listByTenant(@Param("tenantId") Long tenantId,
                                            @Param("status") String status,
                                            @Param("executorId") Long executorId,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);

    List<InspectionTaskEntity> listOverdue(@Param("tenantId") Long tenantId,
                                           @Param("now") LocalDateTime now);

    int markMissed(@Param("tenantId") Long tenantId,
                   @Param("taskId") Long taskId,
                   @Param("now") LocalDateTime now);

    Map<String, Object> aggregateStatistics(@Param("tenantId") Long tenantId,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);

    long countPendingInWindow(@Param("tenantId") Long tenantId,
                              @Param("planId") Long planId,
                              @Param("windowStart") LocalDateTime windowStart,
                              @Param("windowEnd") LocalDateTime windowEnd);
}
