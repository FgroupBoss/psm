package com.fgroupboss.ai.psm.operation.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("permit_approval_record")
public class PermitApprovalRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private Long instanceId;
    private Long nodeInstanceId;
    private Integer nodeSeq;
    private String nodeName;
    private String signMode;
    private Long taskId;
    private Long assigneeUserId;
    private String action;
    private String opinion;
    private String operatorName;
    private Date operatedAt;
}
