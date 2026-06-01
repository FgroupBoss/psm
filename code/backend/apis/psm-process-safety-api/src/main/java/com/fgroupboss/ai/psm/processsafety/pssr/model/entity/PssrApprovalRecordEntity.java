package com.fgroupboss.ai.psm.processsafety.pssr.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pssr_approval_record")
public class PssrApprovalRecordEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long projectId;
    private Long approverUserId;
    private String decision;
    private String commentText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
