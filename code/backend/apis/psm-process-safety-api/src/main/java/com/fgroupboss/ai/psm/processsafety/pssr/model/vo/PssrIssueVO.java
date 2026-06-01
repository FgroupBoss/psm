package com.fgroupboss.ai.psm.processsafety.pssr.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class PssrIssueVO {
    private Long id;
    private Long tenantId;
    private Long projectId;
    private Long checkItemId;
    private String issueLevel;
    private String description;
    private Long ownerUserId;
    private LocalDateTime dueAt;
    private Integer closeRequiredBeforeStartup;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
