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
    private String inspectionServiceUrl = "http://localhost:18102";
    private String dualPreventionServiceUrl = "http://localhost:18101";
    private String locationServiceUrl = "http://localhost:18103";
    private String videoServiceUrl = "http://localhost:18104";
    private String integrationServiceUrl = "http://localhost:18091";
    private String phaServiceUrl = "http://localhost:18111";
    private String mocServiceUrl = "http://localhost:18112";
    private String pssrServiceUrl = "http://localhost:18113";
    private String barrierServiceUrl = "http://localhost:18114";
    private String incidentServiceUrl = "http://localhost:18115";
    private String governanceServiceUrl = "http://localhost:18116";

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

    public String getInspectionServiceUrl() {
        return inspectionServiceUrl;
    }

    public void setInspectionServiceUrl(String inspectionServiceUrl) {
        this.inspectionServiceUrl = trimTrailingSlash(inspectionServiceUrl);
    }

    public String getDualPreventionServiceUrl() {
        return dualPreventionServiceUrl;
    }

    public void setDualPreventionServiceUrl(String dualPreventionServiceUrl) {
        this.dualPreventionServiceUrl = trimTrailingSlash(dualPreventionServiceUrl);
    }

    public String getLocationServiceUrl() {
        return locationServiceUrl;
    }

    public void setLocationServiceUrl(String locationServiceUrl) {
        this.locationServiceUrl = trimTrailingSlash(locationServiceUrl);
    }

    public String getVideoServiceUrl() {
        return videoServiceUrl;
    }

    public void setVideoServiceUrl(String videoServiceUrl) {
        this.videoServiceUrl = trimTrailingSlash(videoServiceUrl);
    }

    public String getIntegrationServiceUrl() {
        return integrationServiceUrl;
    }

    public void setIntegrationServiceUrl(String integrationServiceUrl) {
        this.integrationServiceUrl = trimTrailingSlash(integrationServiceUrl);
    }

    public String getPhaServiceUrl() {
        return phaServiceUrl;
    }

    public void setPhaServiceUrl(String phaServiceUrl) {
        this.phaServiceUrl = trimTrailingSlash(phaServiceUrl);
    }

    public String getMocServiceUrl() {
        return mocServiceUrl;
    }

    public void setMocServiceUrl(String mocServiceUrl) {
        this.mocServiceUrl = trimTrailingSlash(mocServiceUrl);
    }

    public String getPssrServiceUrl() {
        return pssrServiceUrl;
    }

    public void setPssrServiceUrl(String pssrServiceUrl) {
        this.pssrServiceUrl = trimTrailingSlash(pssrServiceUrl);
    }

    public String getBarrierServiceUrl() {
        return barrierServiceUrl;
    }

    public void setBarrierServiceUrl(String barrierServiceUrl) {
        this.barrierServiceUrl = trimTrailingSlash(barrierServiceUrl);
    }

    public String getIncidentServiceUrl() {
        return incidentServiceUrl;
    }

    public void setIncidentServiceUrl(String incidentServiceUrl) {
        this.incidentServiceUrl = trimTrailingSlash(incidentServiceUrl);
    }

    public String getGovernanceServiceUrl() {
        return governanceServiceUrl;
    }

    public void setGovernanceServiceUrl(String governanceServiceUrl) {
        this.governanceServiceUrl = trimTrailingSlash(governanceServiceUrl);
    }

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return null;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
