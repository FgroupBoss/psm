package com.fgroupboss.ai.psm.identity.iam.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户视图。
 */
@Data
public class TenantVO {

    private Long id;
    private String tenantCode;
    private String tenantName;
    private String tenantType;
    private String status;
    private Long adminUserId;
    private String dataIsolationMode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
