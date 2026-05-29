package com.fgroupboss.ai.psm.barrier.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("compensating_measure")
public class CompensatingMeasureEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long barrierId;
    private String measureDesc;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
