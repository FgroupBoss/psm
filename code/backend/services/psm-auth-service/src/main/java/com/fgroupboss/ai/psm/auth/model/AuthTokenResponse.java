package com.fgroupboss.ai.psm.auth.model;

import java.time.LocalDateTime;

public class AuthTokenResponse {

    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private LocalDateTime accessExpiresAt;
    private LocalDateTime refreshExpiresAt;
    private AuthUser user;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getAccessExpiresAt() {
        return accessExpiresAt;
    }

    public void setAccessExpiresAt(LocalDateTime accessExpiresAt) {
        this.accessExpiresAt = accessExpiresAt;
    }

    public LocalDateTime getRefreshExpiresAt() {
        return refreshExpiresAt;
    }

    public void setRefreshExpiresAt(LocalDateTime refreshExpiresAt) {
        this.refreshExpiresAt = refreshExpiresAt;
    }

    public AuthUser getUser() {
        return user;
    }

    public void setUser(AuthUser user) {
        this.user = user;
    }
}
