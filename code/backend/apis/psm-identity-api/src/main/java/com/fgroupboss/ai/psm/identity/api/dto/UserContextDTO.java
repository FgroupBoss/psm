package com.fgroupboss.ai.psm.identity.api.dto;

import java.io.Serializable;

/**
 * 用户上下文 DTO — 用于跨服务传递当前用户身份信息。
 */
public class UserContextDTO implements Serializable {

    private Long tenantId;
    private Long userId;
    private String username;
    private String displayName;
    private String sessionId;
    private Long permissionVersion;

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Long getPermissionVersion() { return permissionVersion; }
    public void setPermissionVersion(Long permissionVersion) { this.permissionVersion = permissionVersion; }
}
