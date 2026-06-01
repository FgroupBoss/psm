package com.fgroupboss.ai.psm.processsafety.barrier.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BarrierHealthVO {

    private Long barrierId;
    private BigDecimal healthScore;
    private String status;
    private LocalDateTime snapshotAt;
}
