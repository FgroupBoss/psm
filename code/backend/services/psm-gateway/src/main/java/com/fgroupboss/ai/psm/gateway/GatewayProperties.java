package com.fgroupboss.ai.psm.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "psm.gateway")
public class GatewayProperties {

    private String authServiceUrl = "http://localhost:18081";
    private String iamServiceUrl = "http://localhost:18082";
    private String masterDataServiceUrl = "http://localhost:18083";
    private String configRuleServiceUrl = "http://localhost:18084";
    private String auditServiceUrl = "http://localhost:18094";
    private String contractorServiceUrl = "http://localhost:18085";
    private String majorHazardServiceUrl = "http://localhost:18086";
    private String alarmServiceUrl = "http://localhost:18087";
    private String workPermitServiceUrl = "http://localhost:18088";
    private String mobileBffUrl = "http://localhost:18089";
    private String reportServiceUrl = "http://localhost:18090";
    private String fileServiceUrl = "http://localhost:18092";
    private String notificationServiceUrl = "http://localhost:18093";

    public String getAuthServiceUrl() {
        return authServiceUrl;
    }

    public void setAuthServiceUrl(String authServiceUrl) {
        this.authServiceUrl = trimTrailingSlash(authServiceUrl);
    }

    public String getIamServiceUrl() {
        return iamServiceUrl;
    }

    public void setIamServiceUrl(String iamServiceUrl) {
        this.iamServiceUrl = trimTrailingSlash(iamServiceUrl);
    }

    public String getMasterDataServiceUrl() {
        return masterDataServiceUrl;
    }

    public void setMasterDataServiceUrl(String masterDataServiceUrl) {
        this.masterDataServiceUrl = trimTrailingSlash(masterDataServiceUrl);
    }

    public String getConfigRuleServiceUrl() {
        return configRuleServiceUrl;
    }

    public void setConfigRuleServiceUrl(String configRuleServiceUrl) {
        this.configRuleServiceUrl = trimTrailingSlash(configRuleServiceUrl);
    }

    public String getAuditServiceUrl() {
        return auditServiceUrl;
    }

    public void setAuditServiceUrl(String auditServiceUrl) {
        this.auditServiceUrl = trimTrailingSlash(auditServiceUrl);
    }

    public String getContractorServiceUrl() {
        return contractorServiceUrl;
    }

    public void setContractorServiceUrl(String contractorServiceUrl) {
        this.contractorServiceUrl = trimTrailingSlash(contractorServiceUrl);
    }

    public String getMajorHazardServiceUrl() {
        return majorHazardServiceUrl;
    }

    public void setMajorHazardServiceUrl(String majorHazardServiceUrl) {
        this.majorHazardServiceUrl = trimTrailingSlash(majorHazardServiceUrl);
    }

    public String getAlarmServiceUrl() {
        return alarmServiceUrl;
    }

    public void setAlarmServiceUrl(String alarmServiceUrl) {
        this.alarmServiceUrl = trimTrailingSlash(alarmServiceUrl);
    }

    public String getWorkPermitServiceUrl() {
        return workPermitServiceUrl;
    }

    public void setWorkPermitServiceUrl(String workPermitServiceUrl) {
        this.workPermitServiceUrl = trimTrailingSlash(workPermitServiceUrl);
    }

    public String getMobileBffUrl() {
        return mobileBffUrl;
    }

    public void setMobileBffUrl(String mobileBffUrl) {
        this.mobileBffUrl = trimTrailingSlash(mobileBffUrl);
    }

    public String getReportServiceUrl() {
        return reportServiceUrl;
    }

    public void setReportServiceUrl(String reportServiceUrl) {
        this.reportServiceUrl = trimTrailingSlash(reportServiceUrl);
    }

    public String getFileServiceUrl() {
        return fileServiceUrl;
    }

    public void setFileServiceUrl(String fileServiceUrl) {
        this.fileServiceUrl = trimTrailingSlash(fileServiceUrl);
    }

    public String getNotificationServiceUrl() {
        return notificationServiceUrl;
    }

    public void setNotificationServiceUrl(String notificationServiceUrl) {
        this.notificationServiceUrl = trimTrailingSlash(notificationServiceUrl);
    }

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return null;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
