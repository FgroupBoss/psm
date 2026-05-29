package com.fgroupboss.ai.psm.barrier.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class BarrierElementVO {
    private Long id;
    private Long tenantId;
    private Long barrierId;
    private String elementName;
    private String elementType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
