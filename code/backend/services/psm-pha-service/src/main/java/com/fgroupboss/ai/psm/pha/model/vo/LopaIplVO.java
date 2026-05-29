package com.fgroupboss.ai.psm.pha.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LopaIplVO {
    private Long id;
    private Long tenantId;
    private Long scenarioId;
    private String iplName;
    private java.math.BigDecimal pfd;
    private String iplType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}