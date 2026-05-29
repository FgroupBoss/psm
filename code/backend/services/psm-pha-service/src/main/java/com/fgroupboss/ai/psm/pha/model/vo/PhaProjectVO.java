package com.fgroupboss.ai.psm.pha.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PhaProjectVO {
    private Long id;
    private Long tenantId;
    private String projectNo;
    private String projectName;
    private Long siteId;
    private Long unitId;
    private Long majorHazardId;
    private String method;
    private String version;
    private LocalDateTime reviewDueAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}