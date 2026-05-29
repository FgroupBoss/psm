package com.fgroupboss.ai.psm.pha.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PhaNodeVO {
    private Long id;
    private Long tenantId;
    private Long projectId;
    private String nodeNo;
    private String nodeName;
    private String designIntent;
    private String parameters;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}