package com.fgroupboss.ai.psm.integration.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegReportTaskVO {

    private Long id;
    private Long tenantId;
    private String taskNo;
    private String platformCode;
    private String dataDomain;
    private String triggerType;
    private LocalDateTime dataWindowStart;
    private LocalDateTime dataWindowEnd;
    private Integer recordCount;
    private String status;
    private String payloadDigest;
    private LocalDateTime executedAt;
    private LocalDateTime createdAt;
}
