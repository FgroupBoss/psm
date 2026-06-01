package com.fgroupboss.ai.psm.incidentgovernance.governance.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 指标快照查询参数。
 */
@Data
public class MetricSnapshotQueryParams {

    private Long tenantId;
    private String metricCode;
    private Long siteId;
    private LocalDateTime periodStartFrom;
    private LocalDateTime periodStartTo;
    private int pageNo = 1;
    private int pageSize = 20;
}
