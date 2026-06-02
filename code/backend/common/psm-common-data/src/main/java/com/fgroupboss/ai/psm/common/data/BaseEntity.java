package com.fgroupboss.ai.psm.common.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类 — 只提供字段定义，不绑定 MyBatis-Plus 注解。
 * 各服务 Entity 自行添加 @TableId / @TableName 等注解。
 */
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    protected Long id;
    protected LocalDateTime createTime;
    protected LocalDateTime updateTime;
}
