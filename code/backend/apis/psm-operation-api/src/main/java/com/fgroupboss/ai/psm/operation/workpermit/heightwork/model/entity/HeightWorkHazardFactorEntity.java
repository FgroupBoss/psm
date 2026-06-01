package com.fgroupboss.ai.psm.operation.workpermit.heightwork.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("height_work_hazard_factor")
public class HeightWorkHazardFactorEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String factorCode;
    private String factorName;
    private String hitSource;
    private String controlMeasure;
    private String attachmentRef;
    private String confirmedBy;
    private Date confirmedAt;
    private Date createdAt;
    private Integer deleted;
}
