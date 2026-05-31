package com.fgroupboss.ai.psm.operation.contractor.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class WorkerCertificateRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "certType is required")
    private String certType;
    private String certNo;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Long fileId;
}
