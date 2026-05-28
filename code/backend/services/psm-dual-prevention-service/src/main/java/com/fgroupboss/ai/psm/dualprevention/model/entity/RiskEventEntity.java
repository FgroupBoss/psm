package com.fgroupboss.ai.psm.dualprevention.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dp_risk_event")
public class RiskEventEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long riskUnitId;
    private String eventCode;
    private String eventName;
    private String hazardFactors;
    private String possibleConsequence;
    private String inherentRiskLevel;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
