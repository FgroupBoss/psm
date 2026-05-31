package com.fgroupboss.ai.psm.processsafety.pha.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HazopDeviationVO {
    private Long id;
    private Long tenantId;
    private Long nodeId;
    private String parameter;
    private String guideword;
    private String deviationDesc;
    private String riskLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}