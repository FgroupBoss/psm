package com.fgroupboss.ai.psm.contractor.model.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractorQualificationVO {

    private Long id;
    private Long tenantId;
    private Long companyId;
    private String qualType;
    private String qualName;
    private String qualNo;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Integer coreFlag;
    private Long fileId;
    private String status;
    /** 查询时计算：valid_to 早于今日 */
    private Boolean expired;
    /** 核心资质且已过期 */
    private Boolean coreExpired;
}
