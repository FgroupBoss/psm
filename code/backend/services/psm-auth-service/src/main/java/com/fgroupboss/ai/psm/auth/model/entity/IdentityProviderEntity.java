package com.fgroupboss.ai.psm.auth.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("auth_identity_provider")
public class IdentityProviderEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String providerCode;
    private String providerType;
    private String providerName;
    private String clientId;
    private String clientSecret;
    private String authorizeUrl;
    private String tokenUrl;
    private String userInfoUrl;
    private String callbackUrl;
    private String userMappingField;
    private String config;
    private Boolean enabled;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public String getProviderCode() { return providerCode; }
    public void setProviderCode(String providerCode) { this.providerCode = providerCode; }
    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public String getAuthorizeUrl() { return authorizeUrl; }
    public void setAuthorizeUrl(String authorizeUrl) { this.authorizeUrl = authorizeUrl; }
    public String getTokenUrl() { return tokenUrl; }
    public void setTokenUrl(String tokenUrl) { this.tokenUrl = tokenUrl; }
    public String getUserInfoUrl() { return userInfoUrl; }
    public void setUserInfoUrl(String userInfoUrl) { this.userInfoUrl = userInfoUrl; }
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    public String getUserMappingField() { return userMappingField; }
    public void setUserMappingField(String userMappingField) { this.userMappingField = userMappingField; }
    public String getConfig() { return config; }
    public void setConfig(String config) { this.config = config; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
