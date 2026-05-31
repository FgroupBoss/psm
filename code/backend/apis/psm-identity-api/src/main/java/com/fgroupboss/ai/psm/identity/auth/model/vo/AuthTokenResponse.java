package com.fgroupboss.ai.psm.identity.auth.model.vo;

import java.time.LocalDateTime;

public class AuthTokenResponse {

    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private LocalDateTime accessExpiresAt;
    private LocalDateTime refreshExpiresAt;
    private Long permissionVersion;
    private AuthUserVO user;

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

    public Long getPermissionVersion() {
        return permissionVersion;
    }

    public void setPermissionVersion(Long permissionVersion) {
        this.permissionVersion = permissionVersion;
    }

    public AuthUserVO getUser() {
        return user;
    }

    public void setUser(AuthUserVO user) {
        this.user = user;
    }
}
