package com.fgroupboss.ai.psm.governance.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基地映射视图。
 */
@Data
public class GovSiteMappingVO {

    private Long id;
    private Long tenantId;
    private String siteCode;
    private String siteName;
    private Long orgId;
    private Integer enabledFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
