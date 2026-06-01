package com.fgroupboss.ai.psm.operation.workpermit.heightwork.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("height_work_protection_check")
public class HeightWorkProtectionCheckEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String checkStage;
    private String itemCode;
    private String itemName;
    private Integer requiredFlag;
    private String checkResult;
    private String attachmentRef;
    private String locationText;
    private String checkedBy;
    private Date checkedAt;
}
