package com.fgroupboss.ai.psm.mobile.client.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MonitorRecordRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "recordType is required")
    private String recordType;
    private String content;
    private Boolean abnormalFlag;
    private String attachmentRef;
}
