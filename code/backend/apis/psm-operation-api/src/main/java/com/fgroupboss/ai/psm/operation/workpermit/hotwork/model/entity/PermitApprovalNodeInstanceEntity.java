package com.fgroupboss.ai.psm.operation.workpermit.hotwork.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("permit_approval_node_instance")
public class PermitApprovalNodeInstanceEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long instanceId;
    private Integer nodeSeq;
    private String nodeName;
    private String signMode;
    private String status;
    private Integer requiredCount;
    private Integer approvedCount;
    private Date startedAt;
    private Date completedAt;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
