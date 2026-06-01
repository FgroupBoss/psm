package com.fgroupboss.ai.psm.identity.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "psm.gateway")
public class GatewayProperties {

    private String authServiceUrl = "http://127.0.0.1:18080";
    private String iamServiceUrl = "http://127.0.0.1:18080";
    private String masterDataServiceUrl = "http://127.0.0.1:18080";
    private String configRuleServiceUrl = "http://psm-process-safety:18111";
    private String auditServiceUrl = "http://127.0.0.1:18080";
    private String contractorServiceUrl = "http://psm-operation:18088";
    private String majorHazardServiceUrl = "http://psm-risk:18101";
    private String alarmServiceUrl = "http://psm-realtime:18087";
    private String workPermitServiceUrl = "http://psm-operation:18088";
    private String mobileBffUrl = "http://psm-operation:18088";
    private String reportServiceUrl = "http://psm-incident-governance:18115";
    private String fileServiceUrl = "http://127.0.0.1:18080";
    private String notificationServiceUrl = "http://127.0.0.1:18080";
    private String inspectionServiceUrl = "http://psm-risk:18101";
    private String dualPreventionServiceUrl = "http://psm-risk:18101";
    private String locationServiceUrl = "http://psm-realtime:18087";
    private String videoServiceUrl = "http://psm-realtime:18087";
    private String integrationServiceUrl = "http://psm-incident-governance:18115";
    private String phaServiceUrl = "http://psm-process-safety:18111";
    private String mocServiceUrl = "http://psm-process-safety:18111";
    private String pssrServiceUrl = "http://psm-process-safety:18111";
    private String barrierServiceUrl = "http://psm-process-safety:18111";
    private String incidentServiceUrl = "http://psm-incident-governance:18115";
    private String governanceServiceUrl = "http://psm-incident-governance:18115";

    // === 微服务治理目标域服务 URL（灰度切换用） ===
    private String identityServiceUrl = "http://127.0.0.1:18080";
    private String operationControlServiceUrl = "http://psm-operation:18088";
    private String realtimePerceptionServiceUrl = "http://psm-realtime:18087";
    private String riskControlServiceUrl = "http://psm-risk:18101";
    private String processSafetyServiceUrl = "http://psm-process-safety:18111";
    private String incidentGovernanceServiceUrl = "http://psm-incident-governance:18115";

    // === 灰度开关（true = 使用新目标服务, false = 使用原服务） ===
    private boolean useIdentityService = false;
    private boolean useOperationControlService = false;
    private boolean useRealtimePerceptionService = false;
    private boolean useRiskControlService = false;
    private boolean useProcessSafetyService = false;
    private boolean useIncidentGovernanceService = false;

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

    // === 目标域服务 getters/setters ===

    public String getIdentityServiceUrl() { return identityServiceUrl; }
    public void setIdentityServiceUrl(String url) { this.identityServiceUrl = trimTrailingSlash(url); }
    public boolean isUseIdentityService() { return useIdentityService; }
    public void setUseIdentityService(boolean v) { this.useIdentityService = v; }

    public String getOperationControlServiceUrl() { return operationControlServiceUrl; }
    public void setOperationControlServiceUrl(String url) { this.operationControlServiceUrl = trimTrailingSlash(url); }
    public boolean isUseOperationControlService() { return useOperationControlService; }
    public void setUseOperationControlService(boolean v) { this.useOperationControlService = v; }

    public String getRealtimePerceptionServiceUrl() { return realtimePerceptionServiceUrl; }
    public void setRealtimePerceptionServiceUrl(String url) { this.realtimePerceptionServiceUrl = trimTrailingSlash(url); }
    public boolean isUseRealtimePerceptionService() { return useRealtimePerceptionService; }
    public void setUseRealtimePerceptionService(boolean v) { this.useRealtimePerceptionService = v; }

    public String getRiskControlServiceUrl() { return riskControlServiceUrl; }
    public void setRiskControlServiceUrl(String url) { this.riskControlServiceUrl = trimTrailingSlash(url); }
    public boolean isUseRiskControlService() { return useRiskControlService; }
    public void setUseRiskControlService(boolean v) { this.useRiskControlService = v; }

    public String getProcessSafetyServiceUrl() { return processSafetyServiceUrl; }
    public void setProcessSafetyServiceUrl(String url) { this.processSafetyServiceUrl = trimTrailingSlash(url); }
    public boolean isUseProcessSafetyService() { return useProcessSafetyService; }
    public void setUseProcessSafetyService(boolean v) { this.useProcessSafetyService = v; }

    public String getIncidentGovernanceServiceUrl() { return incidentGovernanceServiceUrl; }
    public void setIncidentGovernanceServiceUrl(String url) { this.incidentGovernanceServiceUrl = trimTrailingSlash(url); }
    public boolean isUseIncidentGovernanceService() { return useIncidentGovernanceService; }
    public void setUseIncidentGovernanceService(boolean v) { this.useIncidentGovernanceService = v; }

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return null;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
