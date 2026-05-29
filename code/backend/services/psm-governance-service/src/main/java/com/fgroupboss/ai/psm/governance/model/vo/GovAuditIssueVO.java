package com.fgroupboss.ai.psm.governance.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计问题视图。
 */
@Data
public class GovAuditIssueVO {

    private Long id;
    private Long tenantId;
    private String issueNo;
    private Long siteId;
    private String siteName;
    private String description;
    private Long ownerUserId;
    private LocalDateTime dueAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
