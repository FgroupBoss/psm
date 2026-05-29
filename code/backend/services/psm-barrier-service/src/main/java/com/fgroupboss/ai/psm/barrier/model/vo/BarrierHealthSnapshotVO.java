package com.fgroupboss.ai.psm.barrier.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class BarrierHealthSnapshotVO {
    private Long id;
    private Long tenantId;
    private Long barrierId;
    private java.math.BigDecimal healthScore;
    private LocalDateTime snapshotAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
