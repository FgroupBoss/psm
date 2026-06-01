package com.fgroupboss.ai.psm.operation.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("simops_conflict_item")
public class SimopsConflictItemEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long scanResultId;
    private Long workPermitId;
    private Long relatedWorkPermitId;
    private Long ruleId;
    private String workTypeA;
    private String workTypeB;
    private String action;
    private Integer overlapMinutes;
    private String message;
    private Date createdAt;
}
