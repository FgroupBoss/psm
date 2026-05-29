package com.fgroupboss.ai.psm.governance.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("gov_metric_definition")
public class GovMetricDefinitionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String metricCode;
    private String metricName;
    private String metricDomain;
    private String statisticPeriod;
    private String formulaVersion;
    private BigDecimal targetValue;
    private Long ownerOrgId;
    private Integer enabledFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
