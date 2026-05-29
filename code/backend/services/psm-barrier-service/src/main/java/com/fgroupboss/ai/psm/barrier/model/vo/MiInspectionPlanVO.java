package com.fgroupboss.ai.psm.barrier.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class MiInspectionPlanVO {
    private Long id;
    private Long tenantId;
    private Long equipmentId;
    private String planName;
    private Integer cycleDays;
    private LocalDateTime nextDueAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
