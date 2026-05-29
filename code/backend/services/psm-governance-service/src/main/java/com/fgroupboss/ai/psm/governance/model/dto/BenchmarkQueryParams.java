package com.fgroupboss.ai.psm.governance.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 跨基地对标查询参数。
 */
@Data
public class BenchmarkQueryParams {

    private Long tenantId;
    private String metricCode;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}
