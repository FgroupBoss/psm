package com.fgroupboss.ai.psm.processsafety.moc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("moc_training_requirement")
public class MocTrainingRequirementEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long changeId;
    private String trainingDesc;
    private Long ownerUserId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
