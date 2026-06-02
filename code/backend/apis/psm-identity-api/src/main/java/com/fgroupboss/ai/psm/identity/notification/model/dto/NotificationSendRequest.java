package com.fgroupboss.ai.psm.identity.notification.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class NotificationSendRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "userId is required")
    private Long userId;

    private String requestId;

    private String templateCode;

    private String title;
    private String content;
    private String bizType;
    private Long bizId;
    /** 显式指定通道，如 IN_APP、SMS、EMAIL；为空时仅站内信。 */
    private List<String> channels = new ArrayList<String>();
    private String recipientPhone;
    private String recipientEmail;
    private Map<String, String> variables = new HashMap<String, String>();
}
