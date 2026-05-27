package com.fgroupboss.ai.psm.notification.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
public class NotificationSendRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "userId is required")
    private Long userId;

    private String requestId;

    @NotNull(message = "templateCode is required")
    private String templateCode;

    private String title;
    private String content;
    private String bizType;
    private Long bizId;
    private Map<String, String> variables = new HashMap<String, String>();
}
