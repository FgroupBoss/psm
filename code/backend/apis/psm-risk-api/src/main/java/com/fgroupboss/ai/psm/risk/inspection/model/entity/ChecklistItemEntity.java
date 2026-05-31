package com.fgroupboss.ai.psm.risk.inspection.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("insp_checklist_item")
public class ChecklistItemEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long templateId;
    private String itemCode;
    private String itemName;
    private String itemType;
    private String standardValue;
    private BigDecimal lowerLimit;
    private BigDecimal upperLimit;
    private String unit;
    private String abnormalRule;
    private Integer photoRequired;
    private Integer sortOrder;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
