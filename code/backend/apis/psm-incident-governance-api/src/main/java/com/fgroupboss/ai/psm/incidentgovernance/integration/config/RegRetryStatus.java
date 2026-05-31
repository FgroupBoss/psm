package com.fgroupboss.ai.psm.incidentgovernance.integration.config;

/**
 * 重试队列状态。
 */
public enum RegRetryStatus {
    PENDING,
    PROCESSING,
    DONE,
    EXHAUSTED
}
