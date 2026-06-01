package com.fgroupboss.ai.psm.identity.api.dto;

/**
 * 权限校验请求 — 用于网关或业务服务向身份域查询用户权限。
 */
public class PermissionCheckRequest {

    private Long tenantId;
    private Long userId;
    private String resource;
    private String action;

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
