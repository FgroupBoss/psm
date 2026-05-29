package com.fgroupboss.ai.psm.barrier.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class BarrierVO {
    private Long id;
    private Long tenantId;
    private String barrierCode;
    private String barrierName;
    private String barrierType;
    private Long majorHazardId;
    private Long hazopScenarioId;
    private Long ownerOrgId;
    private java.math.BigDecimal healthScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
