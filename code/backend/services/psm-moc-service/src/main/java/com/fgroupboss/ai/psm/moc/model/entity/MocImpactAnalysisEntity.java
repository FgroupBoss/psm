package com.fgroupboss.ai.psm.moc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("moc_impact_analysis")
public class MocImpactAnalysisEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long changeId;
    private String discipline;
    private String impactDesc;
    private String riskLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
