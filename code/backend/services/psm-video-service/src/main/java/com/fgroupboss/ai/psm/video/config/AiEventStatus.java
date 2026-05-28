package com.fgroupboss.ai.psm.video.config;

/**
 * AI 事件处理状态。
 */
public final class AiEventStatus {

    public static final String NEW = "NEW";
    public static final String ALARMED = "ALARMED";
    public static final String IGNORED = "IGNORED";

    private AiEventStatus() {
    }

    public static void assertCanAlarm(String status) {
        if (!NEW.equals(status)) {
            throw new com.fgroupboss.ai.psm.common.BusinessException(400,
                    "only NEW events can be converted to alarm, current: " + status);
        }
    }

    public static void assertCanIgnore(String status) {
        if (!NEW.equals(status)) {
            throw new com.fgroupboss.ai.psm.common.BusinessException(400,
                    "only NEW events can be ignored, current: " + status);
        }
    }
}
