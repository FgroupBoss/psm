package com.fgroupboss.ai.psm.common.data;

import lombok.Getter;
import lombok.Setter;

/**
 * 多租户实体基类 — 继承 BaseEntity，增加租户隔离字段。
 */
@Getter
@Setter
public abstract class TenantEntity extends BaseEntity {

    protected Long tenantId;
}
