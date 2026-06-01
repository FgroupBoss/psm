package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class GasTestRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "gasName is required")
    private String gasName;
    private String measuredValue;
    private String unit;
    @NotNull(message = "qualified is required")
    private Boolean qualified;
    private Date testedAt;
    private String testerName;
    private String remark;
}

