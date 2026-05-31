package com.fgroupboss.ai.psm.processsafety.pha.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("hazop_cause")
public class HazopCauseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long deviationId;
    private String causeDesc;
    private String frequency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}