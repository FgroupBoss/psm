package com.fgroupboss.ai.psm.processsafety.pssr.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pssr_issue")
public class PssrIssueEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long projectId;
    private Long checkItemId;
    private String issueLevel;
    private String description;
    private Long ownerUserId;
    private LocalDateTime dueAt;
    private Integer closeRequiredBeforeStartup;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
