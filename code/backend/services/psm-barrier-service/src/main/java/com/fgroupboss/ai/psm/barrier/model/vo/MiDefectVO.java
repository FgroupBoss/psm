package com.fgroupboss.ai.psm.barrier.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class MiDefectVO {
    private Long id;
    private Long tenantId;
    private String defectNo;
    private Long equipmentId;
    private String defectLevel;
    private String sourceType;
    private String description;
    private LocalDateTime repairDeadline;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
