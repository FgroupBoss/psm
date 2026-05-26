package com.fgroupboss.ai.psm.configrule.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 一期配置对象类型。
 */
public enum ConfigType {

    DICTIONARY("dictionaries", "DICTIONARY"),
    FORM_TEMPLATE("forms", "FORM_TEMPLATE"),
    WORKFLOW_TEMPLATE("workflows", "WORKFLOW_TEMPLATE"),
    RULE_DEFINITION("rules", "RULE_DEFINITION"),
    NOTIFICATION_TEMPLATE("notifications/templates", "NOTIFICATION_TEMPLATE"),
    ATTACHMENT_POLICY("attachments/policies", "ATTACHMENT_POLICY");

    private final String path;
    private final String code;

    ConfigType(String path, String code) {
        this.path = path;
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public String getCode() {
        return code;
    }

    public static ConfigType fromPath(String path) {
        for (ConfigType type : values()) {
            if (type.path.equals(path)) {
                return type;
            }
        }
        throw new BusinessException(400, "unsupported config type: " + path);
    }
}
