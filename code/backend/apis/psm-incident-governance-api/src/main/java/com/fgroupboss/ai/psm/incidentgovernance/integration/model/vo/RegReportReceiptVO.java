package com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegReportReceiptVO {

    private Long id;
    private Long tenantId;
    private Long taskId;
    private String taskNo;
    private Boolean success;
    private String platformCode;
    private String platformMessage;
    private LocalDateTime receiptTime;
}
