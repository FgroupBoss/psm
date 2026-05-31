package com.fgroupboss.ai.psm.processsafety.barrier.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class BarrierDegradationVO {
    private Long id;
    private Long tenantId;
    private Long barrierId;
    private String fromStatus;
    private String toStatus;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
