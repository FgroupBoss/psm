package com.fgroupboss.ai.psm.pha.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("hazop_safeguard")
public class HazopSafeguardEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long deviationId;
    private String safeguardType;
    private String safeguardDesc;
    private String effectiveness;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}