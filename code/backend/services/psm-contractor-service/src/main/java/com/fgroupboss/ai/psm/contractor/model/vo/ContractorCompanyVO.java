package com.fgroupboss.ai.psm.contractor.model.vo;

import lombok.Data;

@Data
public class ContractorCompanyVO {

    private Long id;
    private Long tenantId;
    private String companyCode;
    private String companyName;
    private String contactName;
    private String contactPhone;
    private String businessScope;
    private String status;
    private Integer blacklistFlag;
    private String remark;
}
