package com.fgroupboss.ai.psm.processsafety.pha.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HazopSafeguardVO {
    private Long id;
    private Long tenantId;
    private Long deviationId;
    private String safeguardType;
    private String safeguardDesc;
    private String effectiveness;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}