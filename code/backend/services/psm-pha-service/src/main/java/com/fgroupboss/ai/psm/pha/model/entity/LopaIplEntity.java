package com.fgroupboss.ai.psm.pha.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lopa_ipl")
public class LopaIplEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long scenarioId;
    private String iplName;
    private java.math.BigDecimal pfd;
    private String iplType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}