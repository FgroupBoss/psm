package com.fgroupboss.ai.psm.location.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LocTagRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "tagNo is required")
    private String tagNo;

    private String tagType;
    private String vendorCode;
    private String status;
    private String remark;
}
