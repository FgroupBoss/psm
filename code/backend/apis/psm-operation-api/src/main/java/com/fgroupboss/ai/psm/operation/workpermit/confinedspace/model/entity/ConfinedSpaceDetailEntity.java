package com.fgroupboss.ai.psm.operation.workpermit.confinedspace.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("confined_space_detail")
public class ConfinedSpaceDetailEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private Long spaceId;
    private String spaceName;
    private Integer entryCount;
    private String ventilationType;
    private Integer continuousMonitoring;
    private String ruleVersion;
    private Date validUntil;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
