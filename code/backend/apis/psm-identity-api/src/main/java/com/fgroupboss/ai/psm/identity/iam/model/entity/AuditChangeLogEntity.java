package com.fgroupboss.ai.psm.identity.iam.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 关键数据变更审计实体。
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
    private String clientIp;
    private String userAgent;
    private LocalDateTime operatedAt;
}
