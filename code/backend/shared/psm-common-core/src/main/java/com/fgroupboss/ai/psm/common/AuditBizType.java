package com.fgroupboss.ai.psm.common;

/**
 * 审计 {@code audit_change_log.biz_type} 枚举，第 2 迭代起用于承包商与重大危险源。
 * <p>
 * 命名约定：{@code CONTRACTOR_*}、{@code MAJOR_HAZARD_*}，写入审计时须与本枚举一致。
 */
public enum AuditBizType {

    CONTRACTOR_COMPANY,
    CONTRACTOR_QUALIFICATION,
    CONTRACTOR_WORKER,
    CONTRACTOR_CERTIFICATE,
    CONTRACTOR_TRAINING,
    CONTRACTOR_VIOLATION,
    CONTRACTOR_BLACKLIST,

    MAJOR_HAZARD,
    MAJOR_HAZARD_RESPONSIBILITY,
    MAJOR_HAZARD_POINT,
    MAJOR_HAZARD_ATTACHMENT,

    ALARM,
    ALARM_ACTION,
    ALARM_RULE;

    public String code() {
        return name();
    }
}
