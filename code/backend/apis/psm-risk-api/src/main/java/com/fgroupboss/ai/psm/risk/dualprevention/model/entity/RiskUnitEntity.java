package com.fgroupboss.ai.psm.risk.dualprevention.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dp_risk_unit")
public class RiskUnitEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long parentId;
    private String unitCode;
    private String unitName;
    private Long areaId;
    private Long equipmentId;
    private Long majorHazardId;
    private String inherentRiskLevel;
    private String residualRiskLevel;
    private Long ownerOrgId;
    private Long ownerUserId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
