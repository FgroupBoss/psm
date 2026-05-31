package com.fgroupboss.ai.psm.processsafety.barrier.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class MiInspectionRecordVO {
    private Long id;
    private Long tenantId;
    private Long planId;
    private Long equipmentId;
    private LocalDateTime inspectedAt;
    private String result;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
