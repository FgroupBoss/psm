package com.fgroupboss.ai.psm.operation.workpermit.heightwork.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("height_work_environment_check")
public class HeightWorkEnvironmentCheckEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String checkStage;
    private String windLevel;
    private String weatherType;
    private BigDecimal temperatureC;
    private BigDecimal visibilityM;
    private BigDecimal illuminationLux;
    private String groundCondition;
    private Integer powerProximityFlag;
    private String dataSource;
    private Date sourceSampledAt;
    private String checkResult;
    private String reviewReason;
    private String checkedBy;
    private Date checkedAt;
}
