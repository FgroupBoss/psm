package com.fgroupboss.ai.psm.integration.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegPlatformConfigVO {

    private Long id;
    private Long tenantId;
    private String platformCode;
    private String platformName;
    private String baseUrl;
    private String authType;
    private String credentialRef;
    private Integer enabled;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
