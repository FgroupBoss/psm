package com.fgroupboss.ai.psm.common.notification;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 向消息中心发送站内信的请求体。
 */
@Data
public class NotificationSendRequest {

    private Long tenantId;
    private Long userId;
    private String requestId;
    private String templateCode;
    private String title;
    private String content;
    private String bizType;
    private Long bizId;
    private List<String> channels = new ArrayList<String>();
    private String recipientPhone;
    private String recipientEmail;
    private Map<String, String> variables = new HashMap<String, String>();

    public void setVariables(Map<String, String> variables) {
        this.variables = variables == null ? new HashMap<String, String>() : variables;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels == null ? new ArrayList<String>() : channels;
    }
}
