package com.fgroupboss.ai.psm.common.data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类 — 只提供字段定义，不绑定 MyBatis-Plus 注解。
 * 各服务 Entity 自行添加 @TableId / @TableName 等注解。
 */
public abstract class BaseEntity implements Serializable {

    protected Long id;
    protected LocalDateTime createTime;
    protected LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
