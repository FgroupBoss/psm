package com.fgroupboss.ai.psm.incidentgovernance.governance.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 基地映射创建/更新请求。
 */
@Data
public class GovSiteMappingRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "siteCode is required")
    private String siteCode;

    @NotBlank(message = "siteName is required")
    private String siteName;

    private Long orgId;

    private Integer enabledFlag;
}
