package com.fgroupboss.ai.psm.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("permit_risk_analysis")
public class PermitRiskAnalysisEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String hazardDesc;
    private String controlMeasure;
    private String riskLevel;
    private String analystName;
    private Date analyzedAt;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
