package com.fgroupboss.ai.psm.pha.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lopa_scenario")
public class LopaScenarioEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String scenarioNo;
    private Long projectId;
    private Long deviationId;
    private java.math.BigDecimal initiatingEventFrequency;
    private String consequenceSeverity;
    private java.math.BigDecimal targetFrequency;
    private java.math.BigDecimal mitigatedFrequency;
    private String silRecommendation;
    private String calculationVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}