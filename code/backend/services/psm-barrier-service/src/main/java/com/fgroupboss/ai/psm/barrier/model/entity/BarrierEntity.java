package com.fgroupboss.ai.psm.barrier.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("barrier")
public class BarrierEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String barrierCode;
    private String barrierName;
    private String barrierType;
    private Long majorHazardId;
    private Long hazopScenarioId;
    private Long ownerOrgId;
    private java.math.BigDecimal healthScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
