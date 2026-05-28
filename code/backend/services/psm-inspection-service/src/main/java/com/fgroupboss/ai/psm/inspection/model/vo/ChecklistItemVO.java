package com.fgroupboss.ai.psm.inspection.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChecklistItemVO {

    private Long id;
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
}
