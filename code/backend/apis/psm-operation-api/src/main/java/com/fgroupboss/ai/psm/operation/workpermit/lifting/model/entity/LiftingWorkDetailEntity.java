package com.fgroupboss.ai.psm.operation.workpermit.lifting.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("lifting_work_detail")
public class LiftingWorkDetailEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String loadName;
    private BigDecimal loadWeightT;
    private String liftingLevel;
    private Long craneId;
    private BigDecimal radiusM;
    private String liftingPoint;
    private String landingPoint;
    private String planRef;
    private String ruleVersion;
    private Date validUntil;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
