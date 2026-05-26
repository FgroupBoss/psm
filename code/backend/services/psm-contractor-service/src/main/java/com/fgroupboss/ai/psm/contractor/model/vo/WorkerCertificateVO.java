package com.fgroupboss.ai.psm.contractor.model.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkerCertificateVO {

    private Long id;
    private Long tenantId;
    private Long workerId;
    private String certType;
    private String certNo;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Long fileId;
    private String status;
    private Boolean expired;
}
