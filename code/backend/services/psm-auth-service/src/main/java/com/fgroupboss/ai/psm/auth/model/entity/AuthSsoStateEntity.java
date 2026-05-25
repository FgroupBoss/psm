package com.fgroupboss.ai.psm.auth.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("auth_sso_state")
public class AuthSsoStateEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String providerCode;
    private String state;
    private LocalDateTime expiresAt;
    private String redirectAfterLogin;
    private Boolean consumed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public String getProviderCode() { return providerCode; }
    public void setProviderCode(String providerCode) { this.providerCode = providerCode; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public String getRedirectAfterLogin() { return redirectAfterLogin; }
    public void setRedirectAfterLogin(String redirectAfterLogin) { this.redirectAfterLogin = redirectAfterLogin; }
    public Boolean getConsumed() { return consumed; }
    public void setConsumed(Boolean consumed) { this.consumed = consumed; }
}
