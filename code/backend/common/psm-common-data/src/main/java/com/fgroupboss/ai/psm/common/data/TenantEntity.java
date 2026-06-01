package com.fgroupboss.ai.psm.common.data;

/**
 * 多租户实体基类 — 继承 BaseEntity，增加租户隔离字段。
 */
public abstract class TenantEntity extends BaseEntity {

    protected Long tenantId;

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
}
