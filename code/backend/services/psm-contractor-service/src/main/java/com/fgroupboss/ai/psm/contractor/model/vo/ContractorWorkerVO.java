package com.fgroupboss.ai.psm.contractor.model.vo;

import lombok.Data;

@Data
public class ContractorWorkerVO {

    private Long id;
    private Long tenantId;
    private Long companyId;
    private String workerCode;
    private String name;
    private String phoneMasked;
    private String tradeType;
    private String accessStatus;
    private String trainingStatus;
    private String certificateStatus;
    private String gateCardNo;
    private String locationTagNo;
    private String status;
}
