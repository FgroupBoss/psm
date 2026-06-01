package com.fgroupboss.ai.psm.common.data;

/**
 * 审计字段 — 不继承 BaseEntity，允许各服务独立选择是否审计。
 */
public abstract class AuditFields {

    protected String createBy;
    protected String updateBy;
    protected Integer isDeleted;

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
