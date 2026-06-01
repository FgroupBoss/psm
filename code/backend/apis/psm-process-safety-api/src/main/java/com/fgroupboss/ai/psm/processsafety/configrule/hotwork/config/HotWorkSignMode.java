package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.config;

/**
 * 动火审批节点签署模式。
 */
public final class HotWorkSignMode {

    public static final String ANY = "ANY";
    public static final String ALL = "ALL";

    private HotWorkSignMode() {
    }

    public static boolean isValid(String value) {
        return ANY.equals(value) || ALL.equals(value);
    }
}
