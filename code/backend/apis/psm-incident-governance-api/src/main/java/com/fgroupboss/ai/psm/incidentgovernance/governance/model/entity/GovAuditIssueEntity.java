package com.fgroupboss.ai.psm.incidentgovernance.governance.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gov_audit_issue")
public class GovAuditIssueEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String issueNo;
    private Long siteId;
    private String description;
    private Long ownerUserId;
    private LocalDateTime dueAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
