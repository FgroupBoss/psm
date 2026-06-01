package com.fgroupboss.ai.psm.operation.workpermit.tempelectric.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("temporary_electric_inspection")
public class TempElectricInspectionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private Long facilityId;
    private String inspectionResult;
    private String issueDesc;
    private String rectification;
    private String inspectedBy;
    private Date inspectedAt;
    private Date createdAt;
}
