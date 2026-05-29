package com.fgroupboss.ai.psm.pssr.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class PssrProjectVO {
    private Long id;
    private Long tenantId;
    private String pssrNo;
    private String projectName;
    private String sourceType;
    private Long sourceBizId;
    private Long areaId;
    private Long equipmentId;
    private LocalDateTime plannedStartupAt;
    private String approvalStatus;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
