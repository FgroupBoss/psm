package com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 集团模板视图。
 */
@Data
public class GovTemplateVO {

    private Long id;
    private Long tenantId;
    private String templateCode;
    private String templateName;
    private String templateType;
    private String status;
    private String latestVersionNo;
    private String latestVersionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
