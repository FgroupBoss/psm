package com.fgroupboss.ai.psm.governance.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 审计问题创建请求。
 */
@Data
public class GovAuditIssueRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "siteId is required")
    private Long siteId;

    @NotBlank(message = "description is required")
    private String description;

    private Long ownerUserId;
    private LocalDateTime dueAt;
}
