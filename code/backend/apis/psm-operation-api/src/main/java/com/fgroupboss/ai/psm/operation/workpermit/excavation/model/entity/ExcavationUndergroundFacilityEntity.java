package com.fgroupboss.ai.psm.operation.workpermit.excavation.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("excavation_underground_facility")
public class ExcavationUndergroundFacilityEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String facilityType;
    private String ownerUnit;
    private String position;
    private BigDecimal depthM;
    private String detectionMethod;
    private Integer confirmed;
    private Date createdAt;
    private Integer deleted;
}
