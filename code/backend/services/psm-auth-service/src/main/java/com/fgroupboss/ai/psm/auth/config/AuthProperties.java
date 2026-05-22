package com.fgroupboss.ai.psm.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "psm.auth")
public class AuthProperties {

    private long tokenTtlSeconds = 7200L;
    private long refreshTokenTtlSeconds = 604800L;
    private long ssoStateTtlSeconds = 600L;
    private String publicBaseUrl = "http://localhost:18081";

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
}
