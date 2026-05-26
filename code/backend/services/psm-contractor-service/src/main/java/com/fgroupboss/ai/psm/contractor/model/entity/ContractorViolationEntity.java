package com.fgroupboss.ai.psm.contractor.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contractor_violation")
public class ContractorViolationEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long companyId;
    private Long workerId;
    private LocalDateTime violationTime;
    private String violationDesc;
    private String severity;
    private String rectificationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
