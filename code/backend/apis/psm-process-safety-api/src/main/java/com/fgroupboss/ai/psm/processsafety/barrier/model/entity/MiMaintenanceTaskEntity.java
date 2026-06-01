package com.fgroupboss.ai.psm.processsafety.barrier.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mi_maintenance_task")
public class MiMaintenanceTaskEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long defectId;
    private String taskDesc;
    private Long ownerUserId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
