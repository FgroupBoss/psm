package com.fgroupboss.ai.psm.audit.service;

import com.fgroupboss.ai.psm.audit.model.vo.AuditLogRecordVO;
import com.fgroupboss.ai.psm.common.PageResult;

import java.time.LocalDateTime;

/**
 * 审计日志查询服务。
 *
 * <p>接口只暴露查询条件与响应对象，持久化实体和 SQL 细节由实现层封装。</p>
 */
public interface AuditLogService {

    /**
     * 按租户和可选条件分页查询关键数据变更审计日志。
     *
     * @param tenantId 租户 ID
     * @param bizType 业务类型
     * @param bizId 业务数据 ID
     * @param action 操作动作
     * @param operatorName 操作人名称关键字
     * @param startTime 操作开始时间
     * @param endTime 操作结束时间
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页大小，上限 200
     * @return 审计日志分页结果
     */
    PageResult<AuditLogRecordVO> page(Long tenantId, String bizType, Long bizId, String action,
                                      String operatorName, LocalDateTime startTime, LocalDateTime endTime,
                                      int pageNo, int pageSize);
}
