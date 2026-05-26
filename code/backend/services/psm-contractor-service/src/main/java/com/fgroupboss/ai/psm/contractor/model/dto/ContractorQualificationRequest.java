package com.fgroupboss.ai.psm.contractor.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ContractorQualificationRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "qualType is required")
    private String qualType;
    @NotBlank(message = "qualName is required")
    private String qualName;
    private String qualNo;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Boolean coreFlag;
    private Long fileId;
}
