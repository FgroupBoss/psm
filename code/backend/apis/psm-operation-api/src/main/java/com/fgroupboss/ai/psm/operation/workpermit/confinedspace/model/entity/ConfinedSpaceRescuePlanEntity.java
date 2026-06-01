package com.fgroupboss.ai.psm.operation.workpermit.confinedspace.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("confined_space_rescue_plan")
public class ConfinedSpaceRescuePlanEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String planRef;
    private String contact;
    private String equipmentJson;
    private String confirmedBy;
    private Date confirmedAt;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
