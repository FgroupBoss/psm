package com.fgroupboss.ai.psm.contractor.model.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkerTrainingVO {

    private Long id;
    private Long tenantId;
    private Long workerId;
    private String trainingName;
    private String trainingResult;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Long fileId;
    private Boolean expired;
    private Boolean valid;
}
