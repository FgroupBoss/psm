package com.fgroupboss.ai.psm.auth.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("auth_user_identity")
public class AuthUserIdentityEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long userId;
    private String providerCode;
    private String externalUserId;
    private String externalUsername;
    private Boolean enabled;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getProviderCode() { return providerCode; }
    public void setProviderCode(String providerCode) { this.providerCode = providerCode; }
    public String getExternalUserId() { return externalUserId; }
    public void setExternalUserId(String externalUserId) { this.externalUserId = externalUserId; }
    public String getExternalUsername() { return externalUsername; }
    public void setExternalUsername(String externalUsername) { this.externalUsername = externalUsername; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
