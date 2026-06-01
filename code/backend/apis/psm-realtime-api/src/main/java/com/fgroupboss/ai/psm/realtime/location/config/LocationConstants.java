package com.fgroupboss.ai.psm.realtime.location.config;

/**
 * 定位模块枚举常量。
 */
public final class LocationConstants {

    public static final String BINDING_ACTIVE = "ACTIVE";
    public static final String BINDING_UNBOUND = "UNBOUND";
    public static final String ONLINE = "ONLINE";
    public static final String OFFLINE = "OFFLINE";
    public static final String ALARM_SOURCE_TYPE = "LOCATION";

    private LocationConstants() {
    }

    public static boolean isAlarmBridgeEvent(String eventType) {
        return "FORBIDDEN_ENTER".equals(eventType)
                || "OVERCAPACITY".equals(eventType)
                || "SOS".equals(eventType)
                || "STATIC_TIMEOUT".equals(eventType)
                || "OFFLINE".equals(eventType)
                || "FALL".equals(eventType)
                || "OVERSTAY".equals(eventType);
    }
}
