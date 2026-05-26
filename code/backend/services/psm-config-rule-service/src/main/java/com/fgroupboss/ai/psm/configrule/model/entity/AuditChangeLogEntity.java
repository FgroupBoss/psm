package com.fgroupboss.ai.psm.configrule.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 配置变更审计实体。
 */
@Data
@TableName("audit_change_log")
public class AuditChangeLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String operatorName;
    private String action;
    private String bizType;
    private Long bizId;
    private String beforeValue;
    private String afterValue;
    private String result;
    private LocalDateTime operatedAt;
}
