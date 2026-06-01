package com.fgroupboss.ai.psm.realtime.alarm.config;

/**
 * 报警生命周期状态（批次 3 起用于状态机校验）。
 */
public enum AlarmStatus {

    NEW,
    CONFIRMED,
    IN_PROGRESS,
    PENDING_REVIEW,
    CLOSED,
    ESCALATED,
    FALSE_CLOSED;

    public static AlarmStatus from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("alarm status is required");
        }
        return AlarmStatus.valueOf(value.trim().toUpperCase());
    }
}
