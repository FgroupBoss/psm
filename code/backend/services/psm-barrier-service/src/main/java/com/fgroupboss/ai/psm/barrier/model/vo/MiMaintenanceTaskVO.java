package com.fgroupboss.ai.psm.barrier.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class MiMaintenanceTaskVO {
    private Long id;
    private Long tenantId;
    private Long defectId;
    private String taskDesc;
    private Long ownerUserId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
