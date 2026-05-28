package com.fgroupboss.ai.psm.inspection.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class ChecklistItemRequest {

    @NotBlank(message = "itemCode is required")
    private String itemCode;
    @NotBlank(message = "itemName is required")
    private String itemName;
    private String itemType;
    private String standardValue;
    private BigDecimal lowerLimit;
    private BigDecimal upperLimit;
    private String unit;
    private String abnormalRule;
    private Boolean photoRequired;
    private Integer sortOrder;
}
