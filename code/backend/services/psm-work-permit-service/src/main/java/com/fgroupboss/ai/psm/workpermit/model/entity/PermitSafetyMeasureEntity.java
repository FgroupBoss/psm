package com.fgroupboss.ai.psm.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("permit_safety_measure")
public class PermitSafetyMeasureEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String measureCode;
    private String measureName;
    private Integer requiredFlag;
    private String confirmStatus;
    private String confirmBy;
    private Date confirmAt;
    private String remark;
    private String attachmentRef;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
