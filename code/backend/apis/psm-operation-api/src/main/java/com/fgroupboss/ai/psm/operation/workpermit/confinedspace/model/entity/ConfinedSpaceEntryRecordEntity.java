package com.fgroupboss.ai.psm.operation.workpermit.confinedspace.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("confined_space_entry_record")
public class ConfinedSpaceEntryRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private Long workerId;
    private String workerName;
    private String action;
    private Date operatedAt;
    private String location;
    private String operator;
    private Date createdAt;
    private Integer deleted;
}
