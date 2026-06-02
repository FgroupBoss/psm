package com.fgroupboss.ai.psm.identity.notification.outbound;

import lombok.Data;

@Data
public class OutboundSendResult {

    private String status;
    private String providerMsgId;
    private String errorCode;
    private String errorMessage;

    public static OutboundSendResult sent(String providerMsgId) {
        OutboundSendResult result = new OutboundSendResult();
        result.setStatus("SENT");
        result.setProviderMsgId(providerMsgId);
        return result;
    }

    public static OutboundSendResult skipped(String reason) {
        OutboundSendResult result = new OutboundSendResult();
        result.setStatus("SKIPPED");
        result.setErrorCode("SKIPPED");
        result.setErrorMessage(reason);
        return result;
    }

    public static OutboundSendResult failed(String code, String message) {
        OutboundSendResult result = new OutboundSendResult();
        result.setStatus("FAILED");
        result.setErrorCode(code);
        result.setErrorMessage(message);
        return result;
    }
}
