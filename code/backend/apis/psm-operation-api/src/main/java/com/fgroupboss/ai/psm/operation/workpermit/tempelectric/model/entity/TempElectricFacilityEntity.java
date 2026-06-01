package com.fgroupboss.ai.psm.operation.workpermit.tempelectric.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("temporary_electric_facility")
public class TempElectricFacilityEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String facilityType;
    private String facilityNo;
    private String protectionType;
    private String groundingResult;
    private String qrCode;
    private String status;
    private Date createdAt;
    private Integer deleted;
}
