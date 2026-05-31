package com.fgroupboss.ai.psm.processsafety.barrier.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class CompensatingMeasureVO {
    private Long id;
    private Long tenantId;
    private Long barrierId;
    private String measureDesc;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
