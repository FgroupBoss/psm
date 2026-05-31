package com.fgroupboss.ai.psm.identity.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "psm.auth")
public class AuthProperties {

    private long tokenTtlSeconds = 7200L;
    private long refreshTokenTtlSeconds = 604800L;
    private long ssoStateTtlSeconds = 600L;
    private String publicBaseUrl = "http://localhost:18081";
    private String iamServiceUrl = "http://localhost:18082";

    public long getTokenTtlSeconds() {
        return tokenTtlSeconds;
    }

    public void setTokenTtlSeconds(long tokenTtlSeconds) {
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    public long getRefreshTokenTtlSeconds() {
        return refreshTokenTtlSeconds;
    }

    public void setRefreshTokenTtlSeconds(long refreshTokenTtlSeconds) {
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }

    public long getSsoStateTtlSeconds() {
        return ssoStateTtlSeconds;
    }

    public void setSsoStateTtlSeconds(long ssoStateTtlSeconds) {
        this.ssoStateTtlSeconds = ssoStateTtlSeconds;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getIamServiceUrl() {
        return iamServiceUrl;
    }

    public void setIamServiceUrl(String iamServiceUrl) {
        this.iamServiceUrl = trimTrailingSlash(iamServiceUrl);
    }

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return null;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
