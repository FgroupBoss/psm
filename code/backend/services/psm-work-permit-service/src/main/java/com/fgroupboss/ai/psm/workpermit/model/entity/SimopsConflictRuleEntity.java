package com.fgroupboss.ai.psm.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("simops_conflict_rule")
public class SimopsConflictRuleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String workTypeA;
    private String workTypeB;
    private String areaScope;
    private Integer overlapMinutes;
    private String action;
    private Integer enabled;
    private String remark;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
