package com.fgroupboss.ai.psm.contractor.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contractor_audit_record")
public class ContractorAuditRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String targetType;
    private Long targetId;
    private String action;
    private String beforeStatus;
    private String afterStatus;
    private String opinion;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime operatedAt;
}
