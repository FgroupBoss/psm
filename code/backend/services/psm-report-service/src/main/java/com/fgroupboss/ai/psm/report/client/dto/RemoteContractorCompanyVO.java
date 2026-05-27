package com.fgroupboss.ai.psm.report.client.dto;

import lombok.Data;

@Data
public class RemoteContractorCompanyVO {

    private Long id;
    private Long tenantId;
    private String companyCode;
    private String companyName;
    private String status;
    private Integer blacklistFlag;
}
