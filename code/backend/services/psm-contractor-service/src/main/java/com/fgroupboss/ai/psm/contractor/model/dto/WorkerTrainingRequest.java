package com.fgroupboss.ai.psm.contractor.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class WorkerTrainingRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "trainingName is required")
    private String trainingName;
    @NotBlank(message = "trainingResult is required")
    private String trainingResult;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Long fileId;
}
