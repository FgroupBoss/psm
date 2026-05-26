package com.fgroupboss.ai.psm.contractor.config;

/**
 * 作业票选人资格校验失败原因码。
 */
public final class EligibilityReasonCode {

    public static final String COMPANY_NOT_APPROVED = "COMPANY_NOT_APPROVED";
    public static final String COMPANY_SUSPENDED = "COMPANY_SUSPENDED";
    public static final String COMPANY_BLACKLIST = "COMPANY_BLACKLIST";
    public static final String WORKER_NOT_FOUND = "WORKER_NOT_FOUND";
    public static final String WORKER_NOT_APPROVED = "WORKER_NOT_APPROVED";
    public static final String WORKER_SUSPENDED = "WORKER_SUSPENDED";
    public static final String WORKER_BLACKLIST = "WORKER_BLACKLIST";
    public static final String WORKER_CERT_EXPIRED = "WORKER_CERT_EXPIRED";
    public static final String WORKER_TRAINING_INVALID = "WORKER_TRAINING_INVALID";

    private EligibilityReasonCode() {
    }
}
