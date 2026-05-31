package com.fgroupboss.ai.psm.processsafety.moc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("moc_close_condition")
public class MocCloseConditionEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long changeId;
    private String conditionType;
    private Integer requiredFlag;
    private Long ownerUserId;
    private LocalDateTime dueAt;
    private LocalDateTime completedAt;
    private Long evidenceFileId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
