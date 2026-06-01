package com.fgroupboss.ai.psm.incidentgovernance.governance.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("gov_metric_snapshot")
public class GovMetricSnapshotEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String metricCode;
    private Long siteId;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private BigDecimal metricValue;
    private BigDecimal targetValue;
    private LocalDateTime calculationTime;
    private String inputHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
