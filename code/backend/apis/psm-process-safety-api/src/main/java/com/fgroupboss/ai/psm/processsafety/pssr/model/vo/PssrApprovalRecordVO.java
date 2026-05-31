package com.fgroupboss.ai.psm.processsafety.pssr.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class PssrApprovalRecordVO {
    private Long id;
    private Long tenantId;
    private Long projectId;
    private Long approverUserId;
    private String decision;
    private String commentText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
