package com.fgroupboss.ai.psm.mobile.client.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CheckInRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String locationText;
    private String scanCode;
    private String terminalId;
    private String confirmContent;
}
