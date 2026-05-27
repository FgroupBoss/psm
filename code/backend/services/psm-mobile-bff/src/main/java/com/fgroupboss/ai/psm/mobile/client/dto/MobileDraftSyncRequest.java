package com.fgroupboss.ai.psm.mobile.client.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MobileDraftSyncRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "clientDraftId is required")
    private String clientDraftId;
    private Long userId;
    @NotBlank(message = "draftType is required")
    private String draftType;
    private Long bizId;
    private String payloadJson;
}
