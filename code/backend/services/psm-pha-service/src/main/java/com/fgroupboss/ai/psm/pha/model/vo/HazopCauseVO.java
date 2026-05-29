package com.fgroupboss.ai.psm.pha.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HazopCauseVO {
    private Long id;
    private Long tenantId;
    private Long deviationId;
    private String causeDesc;
    private String frequency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}