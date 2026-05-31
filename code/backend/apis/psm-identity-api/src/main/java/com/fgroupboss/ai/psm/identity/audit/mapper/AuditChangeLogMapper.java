package com.fgroupboss.ai.psm.identity.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.audit.model.entity.AuditChangeLogEntity;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditChangeLogMapper extends BaseMapper<AuditChangeLogEntity> {

    List<AuditChangeLogEntity> list(@Param("tenantId") Long tenantId,
                                    @Param("bizType") String bizType,
                                    @Param("bizTypePrefix") String bizTypePrefix,
                                    @Param("bizId") Long bizId,
                                    @Param("action") String action,
                                    @Param("operatorName") String operatorName,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    @Param("limit") int limit,
                                    @Param("offset") int offset);

    long count(@Param("tenantId") Long tenantId,
               @Param("bizType") String bizType,
               @Param("bizTypePrefix") String bizTypePrefix,
               @Param("bizId") Long bizId,
               @Param("action") String action,
               @Param("operatorName") String operatorName,
               @Param("startTime") LocalDateTime startTime,
               @Param("endTime") LocalDateTime endTime);
}
