package com.fgroupboss.ai.psm.operation.workpermit.hotwork.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("permit_approval_task")
public class PermitApprovalTaskEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long nodeInstanceId;
    private Long workPermitId;
    private Long assigneeUserId;
    private String assigneeName;
    private String status;
    private String action;
    private String opinion;
    private Date actedAt;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
