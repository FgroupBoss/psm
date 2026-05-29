package com.fgroupboss.ai.psm.governance.model.dto;

import lombok.Data;

/**
 * 驾驶舱概览查询参数。
 */
@Data
public class DashboardQueryParams {

    private Long tenantId;
    private Integer enabledSitesOnly;
}
