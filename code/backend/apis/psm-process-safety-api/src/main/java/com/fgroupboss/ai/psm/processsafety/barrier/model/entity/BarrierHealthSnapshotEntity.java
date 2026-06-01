package com.fgroupboss.ai.psm.processsafety.barrier.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("barrier_health_snapshot")
public class BarrierHealthSnapshotEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long barrierId;
    private java.math.BigDecimal healthScore;
    private LocalDateTime snapshotAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
