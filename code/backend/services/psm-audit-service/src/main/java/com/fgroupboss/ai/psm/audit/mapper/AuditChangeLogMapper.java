package com.fgroupboss.ai.psm.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.audit.model.entity.AuditChangeLogEntity;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 关键数据变更审计日志持久化操作。
 */
public interface AuditChangeLogMapper extends BaseMapper<AuditChangeLogEntity> {

    /**
     * 按租户和可选条件查询审计日志列表。
     *
     * @param tenantId 租户 ID
     * @param bizType 业务类型
     * @param bizId 业务数据 ID
     * @param action 操作动作
     * @param operatorName 操作人名称关键字
     * @param startTime 操作开始时间
     * @param endTime 操作结束时间
     * @param limit 每页大小
     * @param offset 起始偏移量
     * @return 审计日志实体列表
     */
    List<AuditChangeLogEntity> list(@Param("tenantId") Long tenantId,
                                    @Param("bizType") String bizType,
                                    @Param("bizId") Long bizId,
                                    @Param("action") String action,
                                    @Param("operatorName") String operatorName,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    @Param("limit") int limit,
                                    @Param("offset") int offset);

    /**
     * 按租户和可选条件统计审计日志总数。
     *
     * @param tenantId 租户 ID
     * @param bizType 业务类型
     * @param bizId 业务数据 ID
     * @param action 操作动作
     * @param operatorName 操作人名称关键字
     * @param startTime 操作开始时间
     * @param endTime 操作结束时间
     * @return 满足条件的日志总数
     */
    long count(@Param("tenantId") Long tenantId,
               @Param("bizType") String bizType,
               @Param("bizId") Long bizId,
               @Param("action") String action,
               @Param("operatorName") String operatorName,
               @Param("startTime") LocalDateTime startTime,
               @Param("endTime") LocalDateTime endTime);
}
