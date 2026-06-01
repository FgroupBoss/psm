package com.fgroupboss.ai.psm.risk.inspection.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("insp_task_draft")
public class InspTaskDraftEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String clientDraftId;
    private Long taskId;
    private String payloadJson;
    private String syncStatus;
    private String lastError;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
