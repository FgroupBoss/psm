package com.fgroupboss.ai.psm.barrier.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class MiEquipmentVO {
    private Long id;
    private Long tenantId;
    private String equipmentCode;
    private String equipmentName;
    private Long areaId;
    private Integer criticalFlag;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
