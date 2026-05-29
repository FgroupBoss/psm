package com.fgroupboss.ai.psm.pha.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("hazop_consequence")
public class HazopConsequenceEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long deviationId;
    private String consequenceDesc;
    private String severity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}