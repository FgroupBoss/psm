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

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return null;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
