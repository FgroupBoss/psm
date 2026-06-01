package com.fgroupboss.ai.psm.operation.workpermit.heightwork.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("height_work_detail")
public class HeightWorkDetailEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private BigDecimal workHeightM;
    private String fallDatumDescription;
    private String workLocation;
    private String workMethod;
    private String heightLevel;
    private String riskClass;
    private Integer manualUpgradeFlag;
    private String manualUpgradeReason;
    private String rescuePlanRef;
    private String rescueContact;
    private Integer communicationConfirmed;
    private String ruleVersion;
    private Date validUntil;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
