package com.fgroupboss.ai.psm.operation.workpermit.hotwork.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("permit_approval_instance")
public class PermitApprovalInstanceEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private Long workflowTemplateId;
    private Integer workflowTemplateVersion;
    private String templateSnapshotJson;
    private String status;
    private Integer currentNodeSeq;
    private Date startedAt;
    private Date completedAt;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
