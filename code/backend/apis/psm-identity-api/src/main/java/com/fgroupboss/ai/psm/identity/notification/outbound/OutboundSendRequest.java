package com.fgroupboss.ai.psm.identity.notification.outbound;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 外通道投递请求体，由站内信发送流程组装后交给路由器。
 */
@Data
public class OutboundSendRequest {

    private Long tenantId;
    private Long userId;
    private String channel;
    private String requestId;
    private String templateCode;
    private String title;
    private String content;
    private String recipientPhone;
    private String recipientEmail;
    private Map<String, String> variables = new HashMap<String, String>();
}
