package com.fgroupboss.ai.psm.moc.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MocChangeVO {
    private Long id;
    private Long tenantId;
    private String changeNo;
    private String changeTitle;
    private String changeType;
    private String changeLevel;
    private Integer temporaryFlag;
    private Integer emergencyFlag;
    private Long affectedAreaId;
    private Long affectedEquipmentId;
    private String riskLevel;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
