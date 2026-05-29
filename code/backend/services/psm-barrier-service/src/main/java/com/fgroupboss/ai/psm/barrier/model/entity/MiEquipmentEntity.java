package com.fgroupboss.ai.psm.barrier.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mi_equipment")
public class MiEquipmentEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String equipmentCode;
    private String equipmentName;
    private Long areaId;
    private Integer criticalFlag;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
