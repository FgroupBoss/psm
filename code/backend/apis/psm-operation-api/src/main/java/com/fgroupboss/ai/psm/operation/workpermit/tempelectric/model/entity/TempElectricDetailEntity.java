package com.fgroupboss.ai.psm.operation.workpermit.tempelectric.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("temporary_electric_detail")
public class TempElectricDetailEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private Long sourceId;
    private String voltage;
    private BigDecimal loadKw;
    private Integer hazardousAreaFlag;
    private String planRef;
    private String ruleVersion;
    private Date validUntil;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
