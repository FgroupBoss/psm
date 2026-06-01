package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class ExcavationCountersignRequest {

    private Long tenantId;
    private String specialty;
    private Long signerId;
    private String result;
    private String opinion;
}
