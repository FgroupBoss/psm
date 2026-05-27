package com.fgroupboss.ai.psm.report.client.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RemoteAuditLogVO {

    private Long id;
    private Long tenantId;
    private String operatorName;
    private String action;
    private String bizType;
    private Long bizId;
    private String result;
    private LocalDateTime operatedAt;
}
