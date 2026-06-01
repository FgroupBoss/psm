package com.fgroupboss.ai.psm.operation.workpermit.excavation.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("excavation_countersign")
public class ExcavationCountersignEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String specialty;
    private Long signerId;
    private String result;
    private String opinion;
    private Date signedAt;
    private Date createdAt;
    private Integer deleted;
}
