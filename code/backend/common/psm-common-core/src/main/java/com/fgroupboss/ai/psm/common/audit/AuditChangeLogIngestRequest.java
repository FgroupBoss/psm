package com.fgroupboss.ai.psm.common.audit;

import lombok.Data;

/**
 * 写入中央审计服务 {@code audit_change_log} 的请求体。
 */
@Data
public class AuditChangeLogIngestRequest {

    private Long tenantId;
    private String operatorName;
    private String action;
    private String bizType;
    private Long bizId;
    private String beforeValue;
    private String afterValue;
    private String result;
}
